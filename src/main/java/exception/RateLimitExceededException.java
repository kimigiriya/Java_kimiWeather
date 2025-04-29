package exception;

import java.time.Duration;

public class RateLimitExceededException extends WeatherApiException {
    private final int limit;
    private final Duration resetTime;
    private final String rateLimitType;

    public RateLimitExceededException(String message, String apiUrl, int statusCode) {
        super(message, apiUrl, statusCode);
        this.limit = -1;
        this.resetTime = null;
        this.rateLimitType = null;
    }

    public RateLimitExceededException(String message, String apiUrl, int statusCode,
                                      int limit, Duration resetTime, String rateLimitType) {
        super(message, apiUrl, statusCode);
        this.limit = limit;
        this.resetTime = resetTime;
        this.rateLimitType = rateLimitType;
    }

    public int getLimit() {
        return limit;
    }

    public Duration getResetTime() {
        return resetTime;
    }

    public String getRateLimitType() {
        return rateLimitType;
    }

    @Override
    public String getMessage() {
        String base = super.getMessage();
        if (resetTime != null) {
            return String.format("%s (Лимит: %d/%s, Сброс через: %d мин)",
                    base, limit, rateLimitType, resetTime.toMinutes());
        }
        return base;
    }

    public String getRetryTimeSuggestion() {
        if (resetTime != null) {
            return String.format("Попробуйте снова через %d минут", resetTime.toMinutes());
        }
        return "Попробуйте позже";
    }
}