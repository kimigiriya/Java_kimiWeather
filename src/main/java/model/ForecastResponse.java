package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {
    @SerializedName("list")
    private List<DailyForecast> list;

    public List<DailyForecast> getList() {
        return list;
    }

    public void setList(List<DailyForecast> list) {
        this.list = list;
    }
}