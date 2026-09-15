package au.jefrin.controller;

import au.jefrin.dto.request.RefreshTokenRequest;
import au.jefrin.dto.response.ApiResponse;
import au.jefrin.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;


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
    protected ApiResponse<?> processRequest(RefreshTokenRequest request, HttpServletRequest httpRequest) throws Exception {
        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            throw new IllegalArgumentException("Missing refresh token");
        }

        au.jefrin.dto.response.TokenResponse tokenResponse = authService.refreshTokens(request.getRefreshToken());
        return ApiResponse.success(tokenResponse);
    }
}

