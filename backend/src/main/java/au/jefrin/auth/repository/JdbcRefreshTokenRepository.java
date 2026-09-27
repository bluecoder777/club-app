package au.jefrin.auth.repository;

import au.jefrin.auth.model.RefreshToken;
import au.jefrin.common.config.DatabaseConfig;
import au.jefrin.common.repository.JdbcOperation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Optional;

public class JdbcRefreshTokenRepository implements RefreshTokenRepository {

    @Override
    public void save(RefreshToken refreshToken) {
        JdbcOperation.execute("Failed to save refresh token", () -> {
            String sql = "INSERT INTO refresh_tokens (token, user_id, expires_at, revoked) VALUES (?, ?, ?, ?)";
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, refreshToken.getToken());
                statement.setLong(2, refreshToken.getUserId());
                statement.setObject(3, refreshToken.getExpiresAt());
                statement.setBoolean(4, refreshToken.isRevoked());
                statement.executeUpdate();
            }
        });
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return JdbcOperation.execute("Failed to find refresh token", () -> {
            String sql = "SELECT * FROM refresh_tokens WHERE token = ?";
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, token);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(RefreshToken.builder()
                                .id(resultSet.getLong("id"))
                                .token(resultSet.getString("token"))
                                .userId(resultSet.getLong("user_id"))
                                .expiresAt(resultSet.getObject("expires_at", LocalDateTime.class))
                                .revoked(resultSet.getBoolean("revoked"))
                                .build());
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public void revokeToken(String token) {
        JdbcOperation.execute("Failed to revoke refresh token", () -> {
            String sql = "UPDATE refresh_tokens SET revoked = true WHERE token = ?";
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, token);
                statement.executeUpdate();
            }
        });
    }

    @Override
    public void revokeAllUserTokens(Long userId) {
        JdbcOperation.execute("Failed to revoke user refresh tokens", () -> {
            String sql = "UPDATE refresh_tokens SET revoked = true WHERE user_id = ?";
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, userId);
                statement.executeUpdate();
            }
        });
    }
}
