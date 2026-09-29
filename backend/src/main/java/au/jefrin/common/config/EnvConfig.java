package au.jefrin.common.config;

public class EnvConfig {

    private static final long DEFAULT_ACCESS_EXPIRY_MS = 900_000;
    private static final long DEFAULT_REFRESH_EXPIRY_MS = 604_800_000;

    public static String get(String key) {
        return System.getenv(key);
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

    public static String getJwtAccessSecret() {
        return getRequired("JWT_ACCESS_SECRET");
    }

    public static String getJwtRefreshSecret() {
        return getRequired("JWT_REFRESH_SECRET");
    }

    public static long getJwtAccessExpiry() {
        String expiry = get("JWT_ACCESS_EXPIRY");
        if (expiry == null || expiry.trim().isEmpty()) {
            return DEFAULT_ACCESS_EXPIRY_MS;
        }
        return Long.parseLong(expiry);
    }

    public static long getJwtRefreshExpiry() {
        String expiry = get("JWT_REFRESH_EXPIRY");
        if (expiry == null || expiry.trim().isEmpty()) {
            return DEFAULT_REFRESH_EXPIRY_MS;
        }
        return Long.parseLong(expiry);
    }

    public static String getFrontendOrigin() {
        String origin = get("FRONTEND_ORIGIN");
        if (origin == null || origin.trim().isEmpty()) {
            return "http://localhost:5173";
        }

        origin = origin.trim();
        while (origin.endsWith("/")) {
            origin = origin.substring(0, origin.length() - 1);
        }
        return origin;
    }

    private static String getRequired(String key) {
        String value = get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("Environment variable missing: " + key);
        }
        return value;
    }
}

