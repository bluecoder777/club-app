package au.jefrin.controller;

import au.jefrin.dto.request.RefreshTokenRequest;
import au.jefrin.dto.response.ApiResponse;
import au.jefrin.service.AuthService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/v1/auth/refresh")
public class RefreshTokenController extends BaseController<RefreshTokenRequest> {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = new AuthService();
    }

    @Override
    protected Class<RefreshTokenRequest> getRequestClass() {
        return RefreshTokenRequest.class;
    }

    @Override
    protected ApiResponse<?> processRequest(RefreshTokenRequest request) throws Exception {
        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            throw new IllegalArgumentException("Missing refresh token");
        }

        au.jefrin.dto.response.TokenResponse tokenResponse = authService.refreshTokens(request.getRefreshToken());
        return ApiResponse.success(tokenResponse);
    }
}

