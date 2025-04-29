import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    private static final String APP_TITLE = "kimiWeather";
    private static final String ICON_PATH = "/images/app_icon.png";
    private static final String MAIN_FXML = "/fxml/main.fxml";
    private static final String STYLES_CSS = "/styles/styles.css";

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_FXML));
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource(STYLES_CSS)).toExternalForm());

            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);

            try {
                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON_PATH)));
                primaryStage.getIcons().add(icon);
            } catch (NullPointerException e) {
                System.err.println("Не удалось загрузить иконку приложения");
            }

            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Ошибка загрузки основного интерфейса:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            System.setProperty("weather.api.key", args[0]);
        }

        launch(args);
    }

    public static String getApiKey() {
        String key = System.getProperty("weather.api.key");
        if (key == null || key.trim().isEmpty()) {
            key = System.getenv("WEATHER_API_KEY");
        }
        return key;
    }
}