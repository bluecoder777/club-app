package au.jefrin.common.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DatabaseConfig.init();

            Flyway flyway = Flyway.configure().dataSource(DatabaseConfig.getDataSource()).load();
            flyway.migrate();
            System.out.println("Flyway Migration completed successfully.");
        } catch (Exception e) {
            sce.getServletContext().log("Failed to initialize database connection pool or migrations", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseConfig.close();
    }
}
