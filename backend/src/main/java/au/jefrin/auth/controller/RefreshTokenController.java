package au.jefrin.auth.controller;

import au.jefrin.auth.dto.TokenResponse;
import au.jefrin.common.controller.BaseController;

import au.jefrin.auth.dto.RefreshTokenRequest;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.auth.service.AuthService;
import au.jefrin.common.config.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/v1/auth/refresh")
public class RefreshTokenController extends BaseController<RefreshTokenRequest> {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = ServiceFactory.createAuthService();
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

        TokenResponse tokenResponse = authService.refreshTokens(request.getRefreshToken());
        return ApiResponse.success(tokenResponse);
    }
}

