package au.jefrin.auth.controller;

import au.jefrin.auth.service.AuthService;
import au.jefrin.common.config.ServiceFactory;
import au.jefrin.common.controller.BaseController;

import au.jefrin.auth.dto.LoginRequest;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.auth.dto.LoginResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/v1/login")
public class LoginController extends BaseController<LoginRequest> {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = ServiceFactory.createAuthService();
    }

    @Override
    protected Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }

    @Override
    protected ApiResponse<?> processRequest(LoginRequest request, HttpServletRequest req) throws Exception {
        if (!request.isValid()) {
            throw new IllegalArgumentException("Missing required fields (email, password)");
        }

        LoginResponse loginResponse = authService.login(request);
        return ApiResponse.success(loginResponse);
    }
}

