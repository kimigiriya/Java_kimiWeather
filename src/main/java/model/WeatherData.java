package model;

import com.google.gson.annotations.SerializedName;
import java.util.Arrays;


public class WeatherData {
    @SerializedName("current")
    private CurrentWeather current;

    @SerializedName("daily")
    private DailyForecast[] daily;

    public WeatherData() {}

    public CurrentWeather getCurrent() {
        return current;
    }

    public void setCurrent(CurrentWeather current) {
        this.current = current;
    }

    public DailyForecast[] getDaily() {
        return daily;
    }

    public void setDaily(DailyForecast[] daily) {
        this.daily = daily;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "current=" + current +
                ", daily=" + Arrays.toString(daily) +
                '}';
    }
}