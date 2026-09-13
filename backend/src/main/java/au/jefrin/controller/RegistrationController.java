package au.jefrin.controller;

import au.jefrin.model.ApiResponse;
import au.jefrin.model.RegistrationRequest;
import au.jefrin.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/v1/register")
public class RegistrationController extends HttpServlet {

    private UserService userService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            RegistrationRequest request = objectMapper.readValue(req.getInputStream(), RegistrationRequest.class);

            if (!request.isValid()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ApiResponse response = new ApiResponse("error", "Missing required fields (name, email, password)");
                objectMapper.writeValue(resp.getWriter(), response);
                return;
            }

            userService.registerUser(request);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            ApiResponse response = new ApiResponse("success", "User registered successfully");
            objectMapper.writeValue(resp.getWriter(), response);

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict for existing resource
            ApiResponse response = new ApiResponse("error", e.getMessage());
            objectMapper.writeValue(resp.getWriter(), response);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ApiResponse response = new ApiResponse("error", "Database error occurred");
            objectMapper.writeValue(resp.getWriter(), response);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ApiResponse response = new ApiResponse("error", "Invalid request payload");
            objectMapper.writeValue(resp.getWriter(), response);
        }
    }
}
