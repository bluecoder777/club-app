package au.jefrin.auth.controller;

import au.jefrin.common.controller.BaseController;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.auth.dto.RegistrationRequest;
import au.jefrin.auth.dto.LoginResponse;
import au.jefrin.auth.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/register")
public class RegistrationController extends BaseController<RegistrationRequest> {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = new AuthService();
    }

    @Override
    protected Class<RegistrationRequest> getRequestClass() {
        return RegistrationRequest.class;
    }

    @Override
    protected int getSuccessStatusCode() {
        return HttpServletResponse.SC_CREATED; // 201 Created for Registration
    }

    @Override
    protected ApiResponse<?> processRequest(RegistrationRequest request, HttpServletRequest req) throws Exception {
        if (!request.isValid()) {
            throw new IllegalArgumentException("Missing required fields (name, email, password)");
        }

        LoginResponse response = authService.register(request);
        return ApiResponse.success(response);
    }
}
