package au.jefrin.auth.repository;

import au.jefrin.auth.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void revokeToken(String token);

    void revokeAllUserTokens(Long userId);
}

