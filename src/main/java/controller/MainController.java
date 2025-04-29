package controller;

import exception.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.City;
import model.CurrentWeather;
import model.DailyForecast;
import service.WeatherService;
import model.DatabaseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.Locale;

public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("E, dd MMM", Locale.getDefault());

    private final WeatherService weatherService;
    private final DatabaseHandler databaseHandler;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @FXML private TextField searchField;
    @FXML private ListView<City> suggestionsList;
    @FXML private Label cityLabel;
    @FXML private ImageView weatherIcon;
    @FXML private Label tempLabel;
    @FXML private Label feelsLikeLabel;
    @FXML private Label minMaxTempLabel;
    @FXML private Label humidityLabel;
    @FXML private Label pressureLabel;
    @FXML private Label windLabel;
    @FXML private Label cloudsLabel;
    @FXML private VBox forecastContainer;
    @FXML private ProgressIndicator progressIndicator;

    public MainController() {
        this.weatherService = createWeatherService();
        this.databaseHandler = DatabaseHandler.getInstance();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private WeatherService createWeatherService() {
        String apiKey = askForApiKey();

        if (apiKey == null || apiKey.trim().isEmpty()) {
            showAlert("Ошибка", "API ключ обязателен для работы приложения");
            Platform.exit();
            return null;
        }

        return new WeatherService(apiKey, true);
    }

    private String askForApiKey() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ввод API ключа");
        dialog.setHeaderText("Введите ваш OpenWeatherMap API ключ");
        dialog.setContentText("Ключ:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    @FXML
    public void initialize() {
        setupSearch();
        loadLastCity();
        setupStyles();
    }

    private void setupStyles() {
        suggestionsList.getStyleClass().add("suggestions-list");
        progressIndicator.setVisible(false);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 3) {
                showProgress(true);
                searchCities(newVal);
            } else {
                suggestionsList.setVisible(false);
            }
        });

        suggestionsList.setCellFactory(lv -> new ListCell<City>() {
            @Override
            protected void updateItem(City city, boolean empty) {
                super.updateItem(city, empty);
                setText(empty ? null : city.getFullName());
            }
        });

        suggestionsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                selectCity(suggestionsList.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void searchCities(String query) {
        executorService.submit(() -> {
            try {
                List<City> cities = weatherService.searchCities(query);
                Platform.runLater(() -> {
                    suggestionsList.getItems().setAll(cities);
                    suggestionsList.setVisible(!cities.isEmpty());
                    showProgress(false);
                });
            } catch (WeatherApiException e) {
                Platform.runLater(() -> {
                    showError("Ошибка поиска", e);
                    showProgress(false);
                });
            }
        });
    }

    private void selectCity(City city) {
        if (city != null) {
            searchField.setText(city.getFullName());
            suggestionsList.setVisible(false);
            loadWeatherData(city);
            databaseHandler.addCity(city);
        }
    }

    private void loadLastCity() {
        executorService.submit(() -> {
            try {
                List<City> cities = databaseHandler.getSavedCities();
                if (!cities.isEmpty()) {
                    City lastCity = cities.get(0);
                    Platform.runLater(() -> loadWeatherData(lastCity));
                }
            } catch (Exception e) {
                logger.error("Ошибка при загрузке последнего города", e);
                Platform.runLater(() -> showError("Ошибка при загрузке последнего города", new WeatherApiException("Ошибка при загрузке последнего города", e))); //  Exception в WeatherApiException
            }
        });
    }

    private void loadWeatherData(City city) {
        showProgress(true);
        executorService.submit(() -> {
            try {
                CurrentWeather current = weatherService.getWeatherData(city.getLatitude(), city.getLongitude());
                List<DailyForecast> forecasts = weatherService.getForecastData(city.getLatitude(), city.getLongitude());  // Получаем прогноз

                Platform.runLater(() -> {
                    if (current != null) {
                        updateCurrentWeather(city, current);
                    } else {
                        logger.warn("Failed to retrieve current weather data for {}", city.getFullName());
                    }
                    if (forecasts != null) {
                        updateForecast(forecasts);
                    } else {
                        logger.warn("Failed to retrieve forecast data for {}", city.getFullName());
                    }
                    showProgress(false);
                });
            } catch (WeatherApiException e) {
                logger.error("Error loading weather data", e);
                Platform.runLater(() -> {
                    showError("Ошибка загрузки данных о погоде", e);
                    showProgress(false);
                });
            }
        });
    }


    private void updateCurrentWeather(City city, CurrentWeather current) {
        if (current == null) {
            logger.warn("CurrentWeather is null. Cannot update weather information.");
            return;
        }
        cityLabel.setText(city.getFullName());
        String iconUrl = "http://openweathermap.org/img/wn/" +
                current.getWeatherIcon() + "@2x.png";
        loadImageAsync(weatherIcon, iconUrl);

        tempLabel.setText(String.format("%.1f°C", current.getMain().getTemp()));
        feelsLikeLabel.setText(String.format("Ощущается как: %.1f°C", current.getMain().getFeelsLike()));
        minMaxTempLabel.setText(String.format("Мин: %.1f°C / Макс: %.1f°C",
                current.getMain().getTempMin(), current.getMain().getTempMax()));
        humidityLabel.setText(String.format("Влажность: %d%%", current.getMain().getHumidity()));
        pressureLabel.setText(String.format("Давление: %d hPa", current.getMain().getPressure()));
        String windDirection = getWindDirection(current.getWindDeg());
        windLabel.setText(String.format("Ветер: %.1f м/с, %s",
                current.getWindSpeed(), windDirection));
        cloudsLabel.setText(String.format("Облачность: %d%%", current.getClouds().getAll()));
    }

    private void updateForecast(List<DailyForecast> forecasts) {
        if (forecasts == null || forecasts.isEmpty()) {
            logger.warn("Forecasts is null or empty.  Cannot update forecast information.");
            forecastContainer.getChildren().clear();
            return;
        }

        forecastContainer.getChildren().clear();
        for (int i = 0; i < 4 && i < forecasts.size(); i++) {
            DailyForecast forecast = forecasts.get(i);
            HBox dayBox = createForecastDayBox(forecast);
            forecastContainer.getChildren().add(dayBox);
        }
    }

    private HBox createForecastDayBox(DailyForecast forecast) {
        HBox box = new HBox(10);
        box.getStyleClass().add("forecast-day");

        Label dateLabel = new Label(forecast.getDate().format(DATE_FORMATTER));
        dateLabel.getStyleClass().add("forecast-date");

        ImageView icon = new ImageView();
        String iconUrl = "http://openweathermap.org/img/wn/" +
                forecast.getWeatherIcon() + ".png";
        loadImageAsync(icon, iconUrl);
        icon.setFitWidth(40);
        icon.setFitHeight(40);

        Label tempLabel = new Label(String.format("%.1f°C (%.1f/%.1f)",
                forecast.getTemperature().getDay(),
                forecast.getTemperature().getMin(),
                forecast.getTemperature().getMax()));

        box.getChildren().addAll(dateLabel, icon, tempLabel);
        return box;
    }

    private void loadImageAsync(ImageView view, String url) {
        new Thread(() -> {
            try {
                Image image = new Image(url, true);
                Platform.runLater(() -> view.setImage(image));
            } catch (Exception e) {
                logger.error("Error loading image from " + url, e);
                Platform.runLater(() -> {
                    view.setImage(null);
                });
            }
        }).start();
    }

    private String getWindDirection(int degrees) {
        String[] directions = {"С", "СВ", "В", "ЮВ", "Ю", "ЮЗ", "З", "СЗ"}; //  Направление ветра
        int index = (int) ((degrees + 22.5) % 360) / 45;
        return directions[index];
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisible(show);
    }

    private void showError(String title, WeatherApiException e) {
        logger.error(title, e);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(e.getMessage());

        if (e instanceof InvalidApiKeyException) {
            alert.setContentText(((InvalidApiKeyException)e).getResolutionSuggestions());
        } else if (e instanceof RateLimitExceededException) {
            alert.setContentText(((RateLimitExceededException)e).getRetryTimeSuggestion());
        }

        alert.showAndWait();
    }

    @FXML
    private void handleRefresh() {
        City current = suggestionsList.getSelectionModel().getSelectedItem();
        if (current != null) {
            loadWeatherData(current);
        }
    }
}