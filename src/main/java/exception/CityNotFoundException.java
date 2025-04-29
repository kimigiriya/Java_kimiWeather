package exception;

public class CityNotFoundException extends WeatherApiException {
    private final String cityName;
    private final String searchContext;

    public CityNotFoundException(String cityName) {
        super(String.format("Город '%s' не найден", cityName));
        this.cityName = cityName;
        this.searchContext = null;
    }

    public CityNotFoundException(String cityName, String searchContext) {
        super(String.format("Город '%s' не найден (%s)", cityName, searchContext));
        this.cityName = cityName;
        this.searchContext = searchContext;
    }

    public CityNotFoundException(String cityName, String apiUrl, int statusCode) {
        super(String.format("Город '%s' не найден", cityName), apiUrl, statusCode);
        this.cityName = cityName;
        this.searchContext = null;
    }

    public String getCityName() {
        return cityName;
    }

    public String getSearchContext() {
        return searchContext;
    }

    public String getResolutionHint() {
        if (searchContext != null) {
            return String.format("Проверьте правильность параметров поиска: %s", searchContext);
        }
        return "Проверьте правильность написания города или попробуйте другой город";
    }
}