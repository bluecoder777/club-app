package au.jefrin.common.repository;

import au.jefrin.common.exception.DataAccessException;

import java.sql.SQLException;

public final class JdbcOperation {

    private JdbcOperation() {
    }

    public static <T> T execute(String message, SqlSupplier<T> operation) {
        try {
            return operation.get();
        } catch (SQLException exception) {
            throw new DataAccessException(message, exception);
        }
    }

    public static void execute(String message, SqlRunnable operation) {
        try {
            operation.run();
        } catch (SQLException exception) {
            throw new DataAccessException(message, exception);
        }
    }

    @FunctionalInterface
    public interface SqlSupplier<T> {
        T get() throws SQLException;
    }

    @FunctionalInterface
    public interface SqlRunnable {
        void run() throws SQLException;
    }
}
