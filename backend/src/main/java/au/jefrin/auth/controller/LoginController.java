package au.jefrin.auth.controller;

import au.jefrin.auth.service.AuthService;
import au.jefrin.common.controller.BaseController;

import au.jefrin.auth.dto.LoginRequest;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.auth.dto.LoginResponse;
import au.jefrin.user.dto.UserResponse;
import au.jefrin.user.model.User;
import au.jefrin.user.service.UserService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/v1/login")
public class LoginController extends BaseController<LoginRequest> {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = new AuthService();
    }

    @Override
    protected Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }

    @Override
    protected ApiResponse<?> processRequest(LoginRequest request, HttpServletRequest req) throws Exception {
        if (!request.isValid()) {
            // This throws an exception which BaseController catches and turns into a 400 Bad Request
            throw new IllegalArgumentException("Missing required fields (email, password)");
        }

        LoginResponse loginResponse = authService.login(request);
        return ApiResponse.success(loginResponse);
    }
}

