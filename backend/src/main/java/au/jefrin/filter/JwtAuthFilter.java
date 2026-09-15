package au.jefrin.filter;

import au.jefrin.dto.response.ApiResponse;
import au.jefrin.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {"/api/v1/clubs/*"})
public class JwtAuthFilter implements Filter {

    private ObjectMapper objectMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
            
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String authHeader = httpRequest.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorizedError(httpResponse, "Missing or invalid Authorization header");
            return;
        }
        
        String token = authHeader.substring(7); // Remove "Bearer "
        
        try {
            if (!JwtUtil.isAccessTokenValid(token)) {
                sendUnauthorizedError(httpResponse, "Invalid or expired access token");
                return;
            }
            
            Long userId = JwtUtil.extractIdFromAccessToken(token);
            // Set the userId in the request attributes so the Controller can easily read it
            httpRequest.setAttribute("userId", userId);
            
            // Proceed to the Controller
            chain.doFilter(request, response);
            
        } catch (Exception e) {
            sendUnauthorizedError(httpResponse, "Invalid access token");
        }
    }

    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        ApiResponse<Void> errorResponse = ApiResponse.error(HttpServletResponse.SC_UNAUTHORIZED, message);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    @Override
    public void destroy() {
    }
}

