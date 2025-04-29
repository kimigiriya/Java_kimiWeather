import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Test_API {
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== OpenWeatherMap API Tester ===");
        System.out.print("Введите ваш API ключ: ");
        String apiKey = scanner.nextLine().trim();

        if (apiKey.isEmpty()) {
            System.out.println("API ключ не может быть пустым!");
            return;
        }

        try {
            String requestUrl = String.format("%s?q=London&appid=%s&units=metric&lang=ru",
                    API_URL, apiKey);

            System.out.println("\nОтправляем запрос: " + requestUrl);

            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            System.out.println("Код ответа: " + responseCode);

            if (responseCode == 200) {
                Scanner responseScanner = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();

                while (responseScanner.hasNextLine()) {
                    response.append(responseScanner.nextLine());
                }
                responseScanner.close();

                System.out.println("\n=== Данные о погоде ===");
                System.out.println("Ответ сервера: " + response.toString());

                String city = extractValue(response.toString(), "\"name\":\"", "\"");
                String temp = extractValue(response.toString(), "\"temp\":", ",");
                String description = extractValue(response.toString(), "\"description\":\"", "\"");

                System.out.println("\nГород: " + city);
                System.out.println("Температура: " + temp + "°C");
                System.out.println("Описание: " + description);
                System.out.println("\nAPI ключ работает корректно, офигеть!");

            } else if (responseCode == 401) {
                System.out.println("Ошибка: Неверный API ключ, ничего удивительного!");
            } else {
                System.out.println("Ошибка: HTTP " + responseCode);
            }

            conn.disconnect();

        } catch (IOException e) {
            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
        }

        scanner.close();
    }

    private static String extractValue(String json, String startKey, String endKey) {
        int startIndex = json.indexOf(startKey);
        if (startIndex == -1) return "N/A";

        startIndex += startKey.length();
        int endIndex = json.indexOf(endKey, startIndex);

        if (endIndex == -1) return "N/A";

        return json.substring(startIndex, endIndex);
    }
}