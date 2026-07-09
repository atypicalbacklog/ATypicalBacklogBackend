package at.atypicalbacklog.management.config;

public enum AppMode {
    MANAGEMENT,
    PUBLIC;

    public static AppMode fromConfig(String value) {
        return "MANAGEMENT".equalsIgnoreCase(value) ? MANAGEMENT : PUBLIC;
    }
}
