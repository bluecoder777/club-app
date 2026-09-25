package au.jefrin.common.controller;

import au.jefrin.common.exception.ConflictException;
import au.jefrin.common.exception.UnauthorizedException;

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

    protected abstract Class<T> getRequestClass();

    protected abstract ApiResponse<?> processRequest(T request, HttpServletRequest req) throws Exception;

    protected int getSuccessStatusCode() {
        return HttpServletResponse.SC_OK;
    }

    private void processHttp(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            T requestPayload;
            if (req.getContentLength() > 0) {
                requestPayload = objectMapper.readValue(req.getInputStream(), getRequestClass());
            } else {
                requestPayload = getRequestClass().getDeclaredConstructor().newInstance();
            }
            
            ApiResponse<?> response = processRequest(requestPayload, req);

            resp.setStatus(getSuccessStatusCode());
            objectMapper.writeValue(resp.getWriter(), response);

        } catch (UnauthorizedException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            objectMapper.writeValue(resp.getWriter(), errorResponse);
            
        } catch (ConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_CONFLICT, e.getMessage());
            objectMapper.writeValue(resp.getWriter(), errorResponse);

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            objectMapper.writeValue(resp.getWriter(), errorResponse);
            
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, "Malformed JSON request payload");
            objectMapper.writeValue(resp.getWriter(), errorResponse);
            
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
            objectMapper.writeValue(resp.getWriter(), errorResponse);
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred");
            objectMapper.writeValue(resp.getWriter(), errorResponse);
        }
    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            processHttp(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processHttp(req, resp);
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
