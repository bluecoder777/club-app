package au.jefrin.auth.service;

import au.jefrin.auth.dto.RegistrationRequest;
import au.jefrin.common.exception.UnauthorizedException;
import au.jefrin.auth.dto.TokenResponse;

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
import java.sql.Timestamp;
import java.util.Objects;

public class AuthService {
    
    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(UserService userService,
                       UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository) {
        this.userService = Objects.requireNonNull(userService, "userService must not be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
        this.refreshTokenRepository = Objects.requireNonNull(
                refreshTokenRepository,
                "refreshTokenRepository must not be null"
        );
    }

    public LoginResponse login(LoginRequest request) throws SQLException {
        User user = userService.authenticate(request);

        refreshTokenRepository.revokeAllUserTokens(user.getId());

        TokenResponse tokenResponse = generateTokensForUser(user);
        
        return LoginResponse.builder()
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .user(UserResponse.fromUser(user))
                .build();
    }

    public TokenResponse refreshTokens(String tokenString) throws SQLException {
        if (!JwtUtil.isRefreshTokenValid(tokenString)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(tokenString);
        if (storedToken == null || storedToken.isRevoked()) {
            throw new UnauthorizedException("Invalid or revoked refresh token");
        }

        String email = JwtUtil.extractEmailFromRefreshToken(tokenString);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        // Rotate refresh tokens so each token can only be used once.
        refreshTokenRepository.revokeToken(tokenString);

        return generateTokensForUser(user);
    }

    public void logout(String refreshToken) throws SQLException {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Refresh token is required for logout");
        }
        
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken);
        
        if (storedToken != null && !storedToken.isRevoked()) {
            refreshTokenRepository.revokeToken(refreshToken);
        }
    }

    private TokenResponse generateTokensForUser(User user) throws SQLException {
        String accessToken = JwtUtil.generateAccessToken(user);
        String refreshTokenString = JwtUtil.generateRefreshToken(user);

        long expirationTime = EnvConfig.getJwtRefreshExpiry();
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + expirationTime);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .userId(user.getId())
                .expiresAt(expiresAt)
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .build();
    }



    public LoginResponse register(RegistrationRequest request) throws SQLException {
        User user = userService.registerUser(request);

        TokenResponse tokenResponse = generateTokensForUser(user);
        
        return LoginResponse.builder()
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .user(UserResponse.fromUser(user))
                .build();
    }
}
