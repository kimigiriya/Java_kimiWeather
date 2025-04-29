package exception;

public class InvalidApiKeyException extends WeatherApiException {
    private final String apiKeyFragment;
    private final String apiEndpoint;
    private final boolean isTrialKey;

    public InvalidApiKeyException(String message) {
        super(message);
        this.apiKeyFragment = null;
        this.apiEndpoint = null;
        this.isTrialKey = false;
    }

    public InvalidApiKeyException(String message, String apiKeyFragment,
                                  String apiEndpoint, boolean isTrialKey) {
        super(message);
        this.apiKeyFragment = apiKeyFragment != null && apiKeyFragment.length() > 5
                ? apiKeyFragment.substring(apiKeyFragment.length() - 5)
                : apiKeyFragment;
        this.apiEndpoint = apiEndpoint;
        this.isTrialKey = isTrialKey;
    }

    public InvalidApiKeyException(String message, String apiKeyFragment,
                                  String apiEndpoint, boolean isTrialKey, Throwable cause) {
        super(message, cause);
        this.apiKeyFragment = apiKeyFragment != null && apiKeyFragment.length() > 5
                ? apiKeyFragment.substring(apiKeyFragment.length() - 5)
                : apiKeyFragment;
        this.apiEndpoint = apiEndpoint;
        this.isTrialKey = isTrialKey;
    }

    public String getApiKeyFragment() {
        return apiKeyFragment;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public boolean isTrialKey() {
        return isTrialKey;
    }

    public String getResolutionSuggestions() {
        StringBuilder sb = new StringBuilder();
        sb.append("Рекомендации:\n");

        if (isTrialKey) {
            sb.append("- Ваш пробный ключ может быть истекшим\n");
            sb.append("- Перейдите на платный тарифный план\n");
        } else {
            sb.append("- Проверьте правильность API-ключа\n");
        }

        sb.append("- Обновите ключ в настройках приложения\n");
        sb.append("- Посетите https://openweathermap.org/api для проверки ключа");

        return sb.toString();
    }

    @Override
    public String getMessage() {
        String base = super.getMessage();
        if (apiEndpoint != null) {
            return String.format("%s [Endpoint: %s, Key: %s]",
                    base,
                    apiEndpoint,
                    apiKeyFragment != null ? "***" + apiKeyFragment : "null");
        }
        return base;
    }
}