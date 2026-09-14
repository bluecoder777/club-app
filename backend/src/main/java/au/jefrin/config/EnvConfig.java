package au.jefrin.config;

/**
 * Centralized configuration manager for Environment Variables.
 * This ensures that if the source of configuration changes in the future
 * (e.g. moving to a config server, .properties files, etc.), only this class needs to be updated.
 */
public class EnvConfig {

    public static String get(String key) {
        return System.getenv(key);
    }

    public static String get(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    public static String getDbUrl() {
        return getRequired("DB_URL");
    }

    public static String getDbUser() {
        String user = get("DB_USERNAME");
        if (user == null || user.trim().isEmpty()) {
            user = get("POSTGRES_USER");
        }
        if (user == null || user.trim().isEmpty()) {
            throw new RuntimeException("Database configuration missing: Ensure DB_USERNAME or POSTGRES_USER is set.");
        }
        return user;
    }

    public static String getDbPassword() {
        String pass = get("DB_PASSWORD");
        if (pass == null || pass.trim().isEmpty()) {
            pass = get("POSTGRES_PASSWORD");
        }
        if (pass == null || pass.trim().isEmpty()) {
            throw new RuntimeException("Database configuration missing: Ensure DB_PASSWORD or POSTGRES_PASSWORD is set.");
        }
        return pass;
    }

    private static String getRequired(String key) {
        String value = get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("Environment variable missing: " + key);
        }
        return value;
    }
}

