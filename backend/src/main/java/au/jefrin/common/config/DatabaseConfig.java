package au.jefrin.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {

    private static HikariDataSource dataSource;

    private DatabaseConfig() {
    }

    public static synchronized void init() {
        if (dataSource == null) {
            try {
                Class.forName("org.postgresql.Driver");

                String dbUrl = EnvConfig.getDbUrl();
                String dbUser = EnvConfig.getDbUser();
                String dbPass = EnvConfig.getDbPassword();

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(dbUrl);
                config.setUsername(dbUser);
                config.setPassword(dbPass);

                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setIdleTimeout(30000);
                config.setConnectionTimeout(20000);
                config.setMaxLifetime(1800000);

                dataSource = new HikariDataSource(config);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize Database Config", e);
            }
        }
    }

    public static javax.sql.DataSource getDataSource() {
        if (dataSource == null) {
            init();
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            init();
        }
        return dataSource.getConnection();
    }

    public static synchronized void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
