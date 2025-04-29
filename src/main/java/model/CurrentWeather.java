package model;

import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CurrentWeather {

    @SerializedName("coord")
    private Coord coord;

    @SerializedName("weather")
    private WeatherCondition[] weatherConditions;

    @SerializedName("base")
    private String base;

    @SerializedName("main")
    private Main main;

    @SerializedName("visibility")
    private int visibility;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("dt")
    private long dt;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("timezone")
    private int timezone;

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("cod")
    private int cod;

    public Coord getCoord() {
        return coord;
    }

    public WeatherCondition[] getWeatherConditions() {
        return weatherConditions;
    }

    public String getBase() {
        return base;
    }

    public Main getMain() {
        return main;
    }

    public int getVisibility() {
        return visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public long getDt() {
        return dt;
    }

    public Sys getSys() {
        return sys;
    }

    public int getTimezone() {
        return timezone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCod() {
        return cod;
    }

    public String getWeatherIcon() {
        return weatherConditions != null && weatherConditions.length > 0 ?
                weatherConditions[0].getIcon() : "";
    }

    public int getWindDeg() {
        return wind != null ? wind.getDeg() : 0;
    }

    public double getWindSpeed() {
        return wind != null ? wind.getSpeed() : 0.0;
    }

    public String getWeatherDescription() {
        return weatherConditions != null && weatherConditions.length > 0 ?
                weatherConditions[0].getDescription() : "";
    }

    public static class Coord {
        @SerializedName("lon")
        private double lon;

        @SerializedName("lat")
        private double lat;

        public double getLon() {
            return lon;
        }

        public double getLat() {
            return lat;
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

    public static class Main {
        @SerializedName("temp")
        private double temp;

        @SerializedName("feels_like")
        private double feelsLike;

        @SerializedName("temp_min")
        private double tempMin;

        @SerializedName("temp_max")
        private double tempMax;

        @SerializedName("pressure")
        private int pressure;

        @SerializedName("humidity")
        private int humidity;

        public double getTemp() {
            return temp;
        }

        public double getFeelsLike() {
            return feelsLike;
        }

        public double getTempMin() {
            return tempMin;
        }

        public double getTempMax() {
            return tempMax;
        }

        public int getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    public static class Wind {
        @SerializedName("speed")
        private double speed;

        @SerializedName("deg")
        private int deg;

        public double getSpeed() {
            return speed;
        }

        public int getDeg() {
            return deg;
        }
    }

    public static class Clouds {
        @SerializedName("all")
        private int all;

        public int getAll() {
            return all;
        }
    }

    public static class Sys {
        @SerializedName("type")
        private int type;

        @SerializedName("id")
        private int id;

        @SerializedName("country")
        private String country;

        @SerializedName("sunrise")
        private long sunrise;

        @SerializedName("sunset")
        private long sunset;

        public int getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        public String getCountry() {
            return country;
        }

        public long getSunrise() {
            return sunrise;
        }

        public long getSunset() {
            return sunset;
        }
    }
}