package au.jefrin.controller;

import au.jefrin.dto.response.ApiResponse;
import au.jefrin.dto.request.RegistrationRequest;
import au.jefrin.service.UserService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/v1/register")
public class RegistrationController extends BaseController<RegistrationRequest> {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
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
    protected ApiResponse<?> processRequest(RegistrationRequest request) throws Exception {
        if (!request.isValid()) {
            throw new IllegalArgumentException("Missing required fields (name, email, password)");
        }

        userService.registerUser(request);
        return ApiResponse.success("User registered successfully");
    }
}
