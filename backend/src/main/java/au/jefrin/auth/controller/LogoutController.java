package au.jefrin.auth.controller;

import au.jefrin.auth.dto.LogoutRequest;
import au.jefrin.auth.service.AuthService;
import au.jefrin.common.config.ServiceFactory;
import au.jefrin.common.controller.BaseController;
import au.jefrin.common.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/v1/logout")
public class LogoutController extends BaseController<LogoutRequest> {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = ServiceFactory.createAuthService();
    }

    @Override
    protected Class<LogoutRequest> getRequestClass() {
        return LogoutRequest.class;
    }

    @Override
    protected ApiResponse<?> processPost(LogoutRequest request, HttpServletRequest req) throws Exception {
        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            throw new IllegalArgumentException("Missing refresh token");
        }

        authService.logout(request.getRefreshToken());

        return ApiResponse.success("Logged out successfully");
    }
}

