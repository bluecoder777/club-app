package au.jefrin.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize the data source when the application starts
        try {
            DatabaseConfig.init();
        } catch (Exception e) {
            sce.getServletContext().log("Failed to initialize database connection pool", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Close the connection pool when the application shuts down
        DatabaseConfig.close();
    }
}

