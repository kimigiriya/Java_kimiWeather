package exception;

public class WeatherApiException extends Exception {
    private final String apiUrl;
    private final int statusCode;

    public WeatherApiException(String message) {
        super(message);
        this.apiUrl = null;
        this.statusCode = -1;
    }

    public WeatherApiException(String message, Throwable cause) {
        super(message, cause);
        this.apiUrl = null;
        this.statusCode = -1;
    }

    public WeatherApiException(String message, String apiUrl, int statusCode) {
        super(message);
        this.apiUrl = apiUrl;
        this.statusCode = statusCode;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        String baseMessage = super.getMessage();
        if (apiUrl != null) {
            return String.format("%s (API: %s, Status: %d)",
                    baseMessage, apiUrl, statusCode);
        }
        return baseMessage;
    }
}