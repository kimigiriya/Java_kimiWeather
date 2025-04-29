package service;

import model.City;
import model.CurrentWeather;
import model.DailyForecast;
import model.ForecastResponse;
import exception.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private static final String GEO_API_URL = "https://api.openweathermap.org/geo/1.0/direct";
    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_API_URL = "https://api.openweathermap.org/data/2.5/forecast/daily";
    private static final int TIMEOUT_SECONDS = 10;

    private final String apiKey;
    private final boolean isTrialKey;
    private final OkHttpClient client;
    private final Gson gson;

    public WeatherService(String apiKey, boolean isTrialKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API ключ не может быть пустым");
        }

        this.apiKey = apiKey;
        this.isTrialKey = isTrialKey;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();

        logger.info("WeatherService создан с API ключем: {}...", apiKey.substring(0, 4) + "****");
    }

    public List<City> searchCities(String query) throws WeatherApiException {
        String url = String.format("%s?q=%s&limit=5&appid=%s",
                GEO_API_URL, query, apiKey);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            validateResponse(response, url);

            String responseBody = response.body().string();
            City[] cities = gson.fromJson(responseBody, City[].class);

            if (cities == null || cities.length == 0) {
                throw new CityNotFoundException(query);
            }

            return Arrays.asList(cities);
        } catch (IOException e) {
            throw new WeatherApiException("Ошибка сети при поиске города", e);
        } catch (JsonSyntaxException e) {
            throw new WeatherApiException("Ошибка парсинга ответа API", e);
        }
    }

    public CurrentWeather getWeatherData(double lat, double lon) throws WeatherApiException {
        String url = String.format("%s?lat=%.4f&lon=%.4f&units=metric&appid=%s",
                WEATHER_API_URL, lat, lon, apiKey);

        logger.debug("getWeatherData URL: {}", url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            validateResponse(response, url);

            String responseBody = response.body().string();

            logger.debug("Current JSON: {}", responseBody);

            CurrentWeather currentWeather = gson.fromJson(responseBody, CurrentWeather.class);

            if (currentWeather == null) {
                logger.warn("getWeatherData: CurrentWeather is null after parsing");
            }

            return currentWeather;
        } catch (IOException e) {
            logger.error("getWeatherData IOException", e);
            throw new WeatherApiException("Ошибка сети при получении погоды", e);
        } catch (JsonSyntaxException e) {
            logger.error("getWeatherData JsonSyntaxException", e);
            throw new WeatherApiException("Ошибка парсинга данных о погоде", e);
        }
    }

    public List<DailyForecast> getForecastData(double lat, double lon) throws WeatherApiException {
        String url = String.format("%s?lat=%.4f&lon=%.4f&cnt=5&units=metric&appid=%s",
                WEATHER_API_URL, lat, lon, apiKey);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            validateResponse(response, url);

            String responseBody = response.body().string();
            ForecastResponse forecastResponse = gson.fromJson(responseBody, ForecastResponse.class);
            logger.info("Forecast JSON: {}", responseBody);
            if (forecastResponse != null && forecastResponse.getList() != null) {
                return forecastResponse.getList();
            } else {
                return Collections.emptyList();
            }
        } catch (IOException e) {
            throw new WeatherApiException("Ошибка сети при получении прогноза", e);
        } catch (JsonSyntaxException e) {
            throw new WeatherApiException("Ошибка парсинга данных о прогнозе", e);
        }
    }

    private void validateResponse(Response response, String url) throws WeatherApiException, IOException {
        if (!response.isSuccessful()) {
            String errorBody = response.body() != null ? response.body().string() : "";
            handleApiError(response.code(), errorBody, url);
        }
    }

    private void handleApiError(int code, String errorBody, String url) throws WeatherApiException {
        String errorMessage = "Неизвестная ошибка API";

        try {
            ApiError error = gson.fromJson(errorBody, ApiError.class);
            if (error != null && error.message != null) {
                errorMessage = error.message;
            }
        } catch (JsonSyntaxException ignored) {}

        switch (code) {

            case 404:
                throw new CityNotFoundException(
                        "Город не найден: " + errorMessage,
                        url,
                        code
                );
            case 429:
                throw new RateLimitExceededException(
                        "Превышен лимит запросов",
                        url,
                        code
                );
            default:
                throw new WeatherApiException(
                        String.format("Ошибка API %d: %s", code, errorMessage),
                        url,
                        code
                );
        }
    }

    private static class ApiError {
        String message;
        String cod;
    }
}