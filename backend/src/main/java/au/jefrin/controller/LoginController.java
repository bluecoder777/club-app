package au.jefrin.controller;

import au.jefrin.dto.request.LoginRequest;
import au.jefrin.dto.response.ApiResponse;
import au.jefrin.dto.response.UserResponse;
import au.jefrin.model.User;
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

@WebServlet("/api/v1/login")
public class LoginController extends HttpServlet {

    private UserService userService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
        this.objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            LoginRequest request = objectMapper.readValue(req.getInputStream(), LoginRequest.class);

            if (!request.isValid()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ApiResponse<Void> response = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields (email, password)");
                objectMapper.writeValue(resp.getWriter(), response);
                return;
            }

            User user = userService.authenticate(request);

            resp.setStatus(HttpServletResponse.SC_OK);
            ApiResponse<UserResponse> response = ApiResponse.success(UserResponse.fromUser(user));
            objectMapper.writeValue(resp.getWriter(), response);

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized for invalid credentials
            ApiResponse<Void> response = ApiResponse.error(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            objectMapper.writeValue(resp.getWriter(), response);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ApiResponse<Void> response = ApiResponse.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
            objectMapper.writeValue(resp.getWriter(), response);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ApiResponse<Void> response = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, "Invalid request payload");
            objectMapper.writeValue(resp.getWriter(), response);
        }
    }
}

