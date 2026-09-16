package au.jefrin.auth.service;

import au.jefrin.common.config.EnvConfig;
import au.jefrin.auth.dto.LoginRequest;
import au.jefrin.auth.dto.LoginResponse;
import au.jefrin.user.dto.UserResponse;
import au.jefrin.auth.model.RefreshToken;
import au.jefrin.user.model.User;
import au.jefrin.auth.repository.RefreshTokenRepository;
import au.jefrin.user.repository.UserRepository;
import au.jefrin.common.util.JwtUtil;

import java.sql.SQLException;
import au.jefrin.user.service.UserService;
import au.jefrin.user.service.UserService;
import java.sql.Timestamp;

public class AuthService {
    
    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService() {
        this.userService = new UserService();
        this.userRepository = new UserRepository();
        this.refreshTokenRepository = new RefreshTokenRepository();
    }

    public LoginResponse login(LoginRequest request) throws SQLException {
        User user = userService.authenticate(request);

        // Security best practice: When logging in freshly, revoke any existing tokens for this user
        refreshTokenRepository.revokeAllUserTokens(user.getId());

        au.jefrin.auth.dto.TokenResponse tokenResponse = generateTokensForUser(user);
        
        return LoginResponse.builder()
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .user(UserResponse.fromUser(user))
                .build();
    }

    public au.jefrin.auth.dto.TokenResponse refreshTokens(String tokenString) throws SQLException {
        if (!JwtUtil.isRefreshTokenValid(tokenString)) {
            throw new au.jefrin.common.exception.UnauthorizedException("Invalid or expired refresh token");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(tokenString);
        if (storedToken == null || storedToken.isRevoked()) {
            throw new au.jefrin.common.exception.UnauthorizedException("Invalid or revoked refresh token");
        }

        String email = JwtUtil.extractEmailFromRefreshToken(tokenString);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new au.jefrin.common.exception.UnauthorizedException("User not found");
        }

        // Revoke the old token now that it has been used (Refresh Token Rotation)
        refreshTokenRepository.revokeToken(tokenString);

        return generateTokensForUser(user);
    }

    public void logout(String refreshToken) throws SQLException {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Refresh token is required for logout");
        }
        
        // Find the token in the database
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken);
        
        if (storedToken != null && !storedToken.isRevoked()) {
            // Revoke the token so it cannot be used to generate new access tokens
            refreshTokenRepository.revokeToken(refreshToken);
        }
    }

    private au.jefrin.auth.dto.TokenResponse generateTokensForUser(User user) throws SQLException {
        String accessToken = JwtUtil.generateAccessToken(user);
        String refreshTokenString = JwtUtil.generateRefreshToken(user);

        // Store the new refresh token in the database
        long expirationTime = EnvConfig.getJwtRefreshExpiry();
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + expirationTime);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .userId(user.getId())
                .expiresAt(expiresAt)
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return au.jefrin.auth.dto.TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .build();
    }
}

