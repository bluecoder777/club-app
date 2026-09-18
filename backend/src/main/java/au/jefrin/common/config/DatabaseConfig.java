package au.jefrin.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {

    private static HikariDataSource dataSource;

    private DatabaseConfig() {
        // Private constructor to prevent instantiation
    }

    public static synchronized void init() {
        if (dataSource == null) {
            try {
                // Ensure driver is loaded
                Class.forName("org.postgresql.Driver");

                // Read configuration through the centralized EnvConfig class
                String dbUrl = EnvConfig.getDbUrl();
                String dbUser = EnvConfig.getDbUser();
                String dbPass = EnvConfig.getDbPassword();

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(dbUrl);
                config.setUsername(dbUser);
                config.setPassword(dbPass);

                // HikariCP recommended settings for PostgreSQL
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

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0 && nodeList.item(0).getTextContent() != null) {
            return nodeList.item(0).getTextContent().trim();
        }
        throw new IllegalArgumentException("Missing required configuration tag: " + tag);
    }
    
    private static String getTagValueOrDefault(String tag, Element element, String defaultValue) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0 && nodeList.item(0).getTextContent() != null) {
            return nodeList.item(0).getTextContent().trim();
        }
        return defaultValue;
    }

    public static javax.sql.DataSource getDataSource() {
        if (dataSource == null) {
            init();
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            init(); // Fallback initialization
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
