package au.jefrin.controller;

import au.jefrin.dto.request.LogoutRequest;
import au.jefrin.dto.response.ApiResponse;
import au.jefrin.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/api/v1/logout")
public class LogoutController extends BaseController<LogoutRequest> {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = new AuthService();
    }

    @Override
    protected Class<LogoutRequest> getRequestClass() {
        return LogoutRequest.class;
    }

    @Override
    protected ApiResponse<?> processRequest(LogoutRequest request) throws Exception {
        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            throw new IllegalArgumentException("Missing refresh token");
        }

        authService.logout(request.getRefreshToken());
        
        return ApiResponse.success("Logged out successfully");
    }
}

