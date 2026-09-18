package au.jefrin.common.controller;

import au.jefrin.common.dto.ApiResponse;
import au.jefrin.common.exception.UnauthorizedException;
import au.jefrin.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

public abstract class AuthenticatedController<T> extends BaseController<T> {

    @Override
    protected ApiResponse<?> processRequest(T request, HttpServletRequest req) throws Exception {
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!JwtUtil.isAccessTokenValid(token)) {
            throw new UnauthorizedException("Invalid or expired access token");
        }

        Long userId = JwtUtil.extractIdFromAccessToken(token);
        req.setAttribute("userId", userId);

        return processAuthenticatedRequest(request, req, userId);
    }

    protected abstract ApiResponse<?> processAuthenticatedRequest(T request, HttpServletRequest req, Long userId) throws Exception;
}
