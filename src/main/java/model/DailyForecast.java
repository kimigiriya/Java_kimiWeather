package model;

import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class DailyForecast {
    @SerializedName("dt")
    private long timestamp;

    @SerializedName("temp")
    private Temperature temperature;

    @SerializedName("humidity")
    private int humidity;

    @SerializedName("pressure")
    private int pressure;

    @SerializedName("wind_speed")
    private double windSpeed;

    @SerializedName("wind_deg")
    private int windDirection;

    @SerializedName("pop")
    private double precipitationProbability;

    @SerializedName("weather")
    private WeatherCondition[] weatherConditions;

    public static class Temperature {
        @SerializedName("day")
        private double day;

        @SerializedName("min")
        private double min;

        @SerializedName("max")
        private double max;

        @SerializedName("night")
        private double night;

        @SerializedName("eve")
        private double evening;

        @SerializedName("morn")
        private double morning;

        public double getDay() {
            return day;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getNight() {
            return night;
        }

        public double getEvening() {
            return evening;
        }

        public double getMorning() {
            return morning;
        }
    }

    public static class WeatherCondition {
        @SerializedName("id")
        private int id;

        @SerializedName("main")
        private String main;

        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        public int getId() {
            return id;
        }

        public String getMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    public LocalDateTime getDate() {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public double getPrecipitationProbability() {
        return precipitationProbability;
    }

    public String getFormattedDate() {
        return getDate().format(DateTimeFormatter.ofPattern("E, dd MMM", Locale.getDefault()));
    }

    public String getWeatherIcon() {
        return weatherConditions != null && weatherConditions.length > 0 ?
                weatherConditions[0].getIcon() : "";
    }

    public String getWeatherDescription() {
        return weatherConditions != null && weatherConditions.length > 0 ?
                weatherConditions[0].getDescription() : "";
    }

    @Override
    public String toString() {
        return String.format("DailyForecast{date=%s, temp=%.1f/%.1f°C, weather=%s}",
                getFormattedDate(),
                temperature != null ? temperature.getMin() : 0,
                temperature != null ? temperature.getMax() : 0,
                getWeatherDescription());
    }
}