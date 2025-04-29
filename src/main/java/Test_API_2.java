import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Test_API_2 {
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/forecast/daily"; // Изменено: Forecast API

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== OpenWeatherMap Forecast API Tester ===");
        System.out.print("Введите ваш API ключ: ");
        String apiKey = scanner.nextLine().trim();

        if (apiKey.isEmpty()) {
            System.out.println("API ключ не может быть пустым!");
            return;
        }

        try {
            String requestUrl = String.format("%s?q=London&appid=%s&units=metric&lang=ru&cnt=4",
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

                System.out.println("\n=== Данные прогноза на 4 дня ===");
                String[] forecasts = extractForecasts(response.toString());

                for (int i = 0; i < forecasts.length; i++) {
                    System.out.println("\n--- День " + (i + 1) + " ---");
                    String date = extractValue(forecasts[i], "\"dt\":", ",");
                    String tempMin = extractValue(forecasts[i], "\"min\":", ",");
                    String tempMax = extractValue(forecasts[i], "\"max\":", ",");
                    String description = extractValue(forecasts[i], "\"description\":\"", "\"");
                    System.out.println("Дата (UTC): " + date);
                    System.out.println("Мин. температура: " + tempMin + "°C");
                    System.out.println("Макс. температура: " + tempMax + "°C");
                    System.out.println("Описание: " + description);
                }

                System.out.println("\nAPI ключ работает корректно для прогноза! Донатер");

            } else if (responseCode == 401) {
                System.out.println("Ошибка: Неверный API ключ для прогноза/скорей всего у Вас бесплатный API ключ");
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

    private static String[] extractForecasts(String json) {
        int listStartIndex = json.indexOf("\"list\":[");
        if (listStartIndex == -1) return new String[0];

        listStartIndex += "\"list\":[".length();
        int listEndIndex = json.indexOf("]", listStartIndex);
        if (listEndIndex == -1) return new String[0];

        String listString = json.substring(listStartIndex, listEndIndex);
        return listString.split("\\},\\{");
    }
}