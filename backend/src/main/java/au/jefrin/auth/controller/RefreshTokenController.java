package au.jefrin.auth.controller;
import au.jefrin.common.controller.BaseController;

import au.jefrin.auth.dto.RefreshTokenRequest;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.auth.service.AuthService;
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
    protected ApiResponse<?> processRequest(RefreshTokenRequest request, HttpServletRequest req) throws Exception {
        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            throw new IllegalArgumentException("Missing refresh token");
        }

        au.jefrin.auth.dto.TokenResponse tokenResponse = authService.refreshTokens(request.getRefreshToken());
        return ApiResponse.success(tokenResponse);
    }
}

