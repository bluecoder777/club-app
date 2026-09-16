package au.jefrin.common.util;

import au.jefrin.common.config.EnvConfig;
import au.jefrin.user.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static SecretKey getAccessSigningKey() {
        String secret = EnvConfig.getJwtAccessSecret();
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private static SecretKey getRefreshSigningKey() {
        String secret = EnvConfig.getJwtRefreshSecret();
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static String generateAccessToken(User user) {
        long expirationTime = EnvConfig.getJwtAccessExpiry();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .claim("name", user.getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getAccessSigningKey())
                .compact();
    }

    public static String generateRefreshToken(User user) {
        long expirationTime = EnvConfig.getJwtRefreshExpiry();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getRefreshSigningKey())
                .compact();
    }

    public static String extractEmailFromRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(getRefreshSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public static boolean isRefreshTokenValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(getRefreshSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    public static Long extractIdFromAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(getAccessSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Long.class);
    }
    
    public static boolean isAccessTokenValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(getAccessSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
