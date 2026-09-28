package au.jefrin.common.filter;

import au.jefrin.common.dto.ApiResponse;
import au.jefrin.common.util.JwtUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthFilter implements Filter {

    public static final String USER_ID_ATTRIBUTE = JwtAuthFilter.class.getName() + ".userId";

    private ObjectMapper objectMapper;

    @Override
    public void init(FilterConfig filterConfig) {
        objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            sendUnauthorizedError(httpResponse, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            sendUnauthorizedError(httpResponse, "Missing or invalid Authorization header");
            return;
        }

        Long userId;
        try {
            userId = JwtUtil.extractIdFromAccessToken(token);
        } catch (JwtException | IllegalArgumentException e) {
            sendUnauthorizedError(httpResponse, "Invalid or expired access token");
            return;
        }

        if (userId == null) {
            sendUnauthorizedError(httpResponse, "Invalid access token");
            return;
        }

        httpRequest.setAttribute(USER_ID_ATTRIBUTE, userId);
        chain.doFilter(request, response);
    }

    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> error = ApiResponse.error(HttpServletResponse.SC_UNAUTHORIZED, message);
        objectMapper.writeValue(response.getWriter(), error);
    }
}
