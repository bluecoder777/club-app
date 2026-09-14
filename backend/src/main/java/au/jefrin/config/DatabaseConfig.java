package au.jefrin.config;

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

                // Load database config from db-config.xml
                InputStream xmlStream = DatabaseConfig.class.getClassLoader().getResourceAsStream("db-config.xml");
                if (xmlStream == null) {
                    throw new RuntimeException("Could not find db-config.xml in resources");
                }

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(xmlStream);
                document.getDocumentElement().normalize();

                Element root = document.getDocumentElement();

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(getTagValue("url", root));
                config.setUsername(getTagValue("username", root));
                config.setPassword(getTagValue("password", root));

                // HikariCP recommended settings for PostgreSQL
                config.setMaximumPoolSize(Integer.parseInt(getTagValueOrDefault("maximumPoolSize", root, "10")));
                config.setMinimumIdle(Integer.parseInt(getTagValueOrDefault("minimumIdle", root, "2")));
                config.setIdleTimeout(Long.parseLong(getTagValueOrDefault("idleTimeout", root, "30000")));
                config.setConnectionTimeout(Long.parseLong(getTagValueOrDefault("connectionTimeout", root, "20000")));
                config.setMaxLifetime(Long.parseLong(getTagValueOrDefault("maxLifetime", root, "1800000")));

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
