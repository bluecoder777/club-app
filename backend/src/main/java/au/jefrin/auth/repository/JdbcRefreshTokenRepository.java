package au.jefrin.auth.repository;

import au.jefrin.auth.model.RefreshToken;
import au.jefrin.common.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcRefreshTokenRepository implements RefreshTokenRepository {

    @Override
    public void save(RefreshToken refreshToken) throws SQLException {
        String sql = "INSERT INTO refresh_tokens (token, user_id, expires_at, revoked) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, refreshToken.getToken());
            statement.setLong(2, refreshToken.getUserId());
            statement.setTimestamp(3, refreshToken.getExpiresAt());
            statement.setBoolean(4, refreshToken.isRevoked());
            statement.executeUpdate();
        }
    }

    @Override
    public RefreshToken findByToken(String token) throws SQLException {
        String sql = "SELECT * FROM refresh_tokens WHERE token = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, token);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return RefreshToken.builder()
                            .id(resultSet.getLong("id"))
                            .token(resultSet.getString("token"))
                            .userId(resultSet.getLong("user_id"))
                            .expiresAt(resultSet.getTimestamp("expires_at"))
                            .revoked(resultSet.getBoolean("revoked"))
                            .build();
                }
            }
        }
        return null;
    }

    @Override
    public void revokeToken(String token) throws SQLException {
        String sql = "UPDATE refresh_tokens SET revoked = true WHERE token = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, token);
            statement.executeUpdate();
        }
    }

    @Override
    public void revokeAllUserTokens(Long userId) throws SQLException {
        String sql = "UPDATE refresh_tokens SET revoked = true WHERE user_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }
}
