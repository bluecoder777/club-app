package au.jefrin.common.controller;

import au.jefrin.common.dto.ApiResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public abstract class BaseController<T> extends HttpServlet {

    protected ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    // Subclasses must provide the class type so Jackson knows what to parse the JSON into
    protected abstract Class<T> getRequestClass();

    // The main business logic method that subclasses will implement
    protected abstract ApiResponse<?> processRequest(T request, HttpServletRequest req) throws Exception;

    // By default, successful POST requests return 200 OK. Subclasses can override this (e.g. 201 Created).
    protected int getSuccessStatusCode() {
        return HttpServletResponse.SC_OK;
    }

    private void processHttp(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // 1. Parse JSON automatically
            T requestPayload;
            if (req.getContentLength() > 0) {
                requestPayload = objectMapper.readValue(req.getInputStream(), getRequestClass());
            } else {
                requestPayload = getRequestClass().getDeclaredConstructor().newInstance();
            }
            
            // 2. Execute Subclass Business Logic
            ApiResponse<?> response = processRequest(requestPayload, req);
            
            // 3. Return Success
            resp.setStatus(getSuccessStatusCode());
            objectMapper.writeValue(resp.getWriter(), response);

        } catch (au.jefrin.common.exception.UnauthorizedException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            objectMapper.writeValue(resp.getWriter(), errorResponse);
            
        } catch (au.jefrin.common.exception.ConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_CONFLICT, e.getMessage());
            objectMapper.writeValue(resp.getWriter(), errorResponse);

        } catch (IllegalArgumentException e) {
            // Business rule violations or validation failures
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            objectMapper.writeValue(resp.getWriter(), errorResponse);
            
        } catch (JsonProcessingException e) {
            // Malformed JSON (e.g., missing quotes)
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, "Malformed JSON request payload");
            objectMapper.writeValue(resp.getWriter(), errorResponse);
            
        } catch (SQLException e) {
            // Database failures
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
            objectMapper.writeValue(resp.getWriter(), errorResponse);
            
        } catch (Exception e) {
            // Catastrophic unhandled errors
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred");
            objectMapper.writeValue(resp.getWriter(), errorResponse);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processHttp(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processHttp(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processHttp(req, resp);
    }

}
