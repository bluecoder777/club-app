package au.jefrin.controller;

import au.jefrin.dto.request.LoginRequest;
import au.jefrin.dto.response.ApiResponse;
import au.jefrin.dto.response.LoginResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;


@WebServlet("/api/v1/login")
public class LoginController extends BaseController<LoginRequest> {

    private au.jefrin.service.AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = new au.jefrin.service.AuthService();
    }

    @Override
    protected Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }

    @Override
    protected ApiResponse<?> processRequest(LoginRequest request, HttpServletRequest httpRequest) throws Exception {
        if (!request.isValid()) {
            // This throws an exception which BaseController catches and turns into a 400 Bad Request
            throw new IllegalArgumentException("Missing required fields (email, password)");
        }

        LoginResponse loginResponse = authService.login(request);
        return ApiResponse.success(loginResponse);
    }
}

