package au.jefrin.auth.repository;

import au.jefrin.auth.model.RefreshToken;

import java.sql.SQLException;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken) throws SQLException;

    RefreshToken findByToken(String token) throws SQLException;

    void revokeToken(String token) throws SQLException;

    void revokeAllUserTokens(Long userId) throws SQLException;
}

