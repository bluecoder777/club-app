package au.jefrin.common.controller;

import au.jefrin.common.dto.ApiResponse;
import au.jefrin.common.exception.ConflictException;
import au.jefrin.common.exception.DataAccessException;
import au.jefrin.common.exception.UnauthorizedException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class BaseController<T> extends HttpServlet {

    protected ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    protected Class<T> getRequestClass() {
        throw new UnsupportedOperationException();
    }

    protected ApiResponse<?> processGet(HttpServletRequest req) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected ApiResponse<?> processPost(T request, HttpServletRequest req) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected ApiResponse<?> processPut(T request, HttpServletRequest req) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected ApiResponse<?> processPatch(T request, HttpServletRequest req) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected ApiResponse<?> processDelete(T request, HttpServletRequest req) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected int getSuccessStatusCode() {
        return HttpServletResponse.SC_OK;
    }

    // keeps api responses and error mapping consistent
    private void processHttp(HttpServletResponse resp, RequestHandler handler) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            ApiResponse<?> response = handler.handle();

            resp.setStatus(getSuccessStatusCode());
            objectMapper.writeValue(resp.getWriter(), response);

        } catch (UnsupportedOperationException e) {
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            ApiResponse<Void> errorResponse = ApiResponse.error(
                    HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "HTTP method is not supported for this endpoint"
            );
            objectMapper.writeValue(resp.getWriter(), errorResponse);

        } catch (UnauthorizedException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            objectMapper.writeValue(resp.getWriter(), errorResponse);

        } catch (ConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            ApiResponse<Void> errorResponse = ApiResponse.error(
                    HttpServletResponse.SC_CONFLICT,
                    e.getErrorKey(),
                    e.getMessage()
            );
            objectMapper.writeValue(resp.getWriter(), errorResponse);

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            objectMapper.writeValue(resp.getWriter(), errorResponse);

        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, "Malformed JSON request payload");
            objectMapper.writeValue(resp.getWriter(), errorResponse);

        } catch (DataAccessException e) {
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

    private T readRequest(HttpServletRequest req) throws Exception {
        if (req.getContentLength() > 0) {
            return objectMapper.readValue(req.getInputStream(), getRequestClass());
        }
        return getRequestClass().getDeclaredConstructor().newInstance();
    }

    // handles patch requests outside the default servlet methods
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            processHttp(resp, () -> processPatch(readRequest(req), req));
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processHttp(resp, () -> processGet(req));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processHttp(resp, () -> processPost(readRequest(req), req));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processHttp(resp, () -> processPut(readRequest(req), req));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processHttp(resp, () -> processDelete(readRequest(req), req));
    }

    @FunctionalInterface
    private interface RequestHandler {
        ApiResponse<?> handle() throws Exception;
    }

}
