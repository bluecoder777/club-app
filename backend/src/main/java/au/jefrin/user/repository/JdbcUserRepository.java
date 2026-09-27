package au.jefrin.user.repository;

import au.jefrin.common.config.DatabaseConfig;
import au.jefrin.common.repository.JdbcOperation;
import au.jefrin.user.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {

    @Override
    public boolean existsByEmail(String email) {
        return JdbcOperation.execute("Failed to check whether the email exists", () -> {
            String query = "SELECT 1 FROM \"user\" WHERE email = ?";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public User save(User user) {
        return JdbcOperation.execute("Failed to save user", () -> {
            String query = "INSERT INTO \"user\" (name, email, password) VALUES (?, ?, ?) RETURNING id";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        user.setId(rs.getLong("id"));
                    }
                }
                return user;
            }
        });
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return JdbcOperation.execute("Failed to find user by email", () -> {
            String query = "SELECT id, name, email, password FROM \"user\" WHERE email = ?";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User();
                        user.setId(rs.getLong("id"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setPassword(rs.getString("password"));
                        return Optional.of(user);
                    }
                }
            }
            return Optional.empty();
        });
    }
}
