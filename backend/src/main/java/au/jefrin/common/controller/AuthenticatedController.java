package au.jefrin.common.controller;

import au.jefrin.common.dto.ApiResponse;
import au.jefrin.common.exception.UnauthorizedException;
import au.jefrin.common.filter.JwtAuthFilter;
import jakarta.servlet.http.HttpServletRequest;

public abstract class AuthenticatedController<T> extends BaseController<T> {

    @Override
    protected final ApiResponse<?> processGet(HttpServletRequest req) throws Exception {
        return processAuthenticatedGet(req, getUserId(req));
    }

    @Override
    protected final ApiResponse<?> processPost(T request, HttpServletRequest req) throws Exception {
        return processAuthenticatedPost(request, req, getUserId(req));
    }

    @Override
    protected final ApiResponse<?> processPut(T request, HttpServletRequest req) throws Exception {
        return processAuthenticatedPut(request, req, getUserId(req));
    }

    @Override
    protected final ApiResponse<?> processPatch(T request, HttpServletRequest req) throws Exception {
        return processAuthenticatedPatch(request, req, getUserId(req));
    }

    @Override
    protected final ApiResponse<?> processDelete(T request, HttpServletRequest req) throws Exception {
        return processAuthenticatedDelete(request, req, getUserId(req));
    }

    private Long getUserId(HttpServletRequest req) {
        Object userIdAttribute = req.getAttribute(JwtAuthFilter.USER_ID_ATTRIBUTE);
        if (!(userIdAttribute instanceof Long userId)) {
            throw new UnauthorizedException("Authentication is required");
        }
        return userId;
    }

    protected ApiResponse<?> processAuthenticatedGet(HttpServletRequest req, Long userId) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected ApiResponse<?> processAuthenticatedPost(T request, HttpServletRequest req, Long userId) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected ApiResponse<?> processAuthenticatedPut(T request, HttpServletRequest req, Long userId) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected ApiResponse<?> processAuthenticatedPatch(T request, HttpServletRequest req, Long userId) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected ApiResponse<?> processAuthenticatedDelete(T request, HttpServletRequest req, Long userId) throws Exception {
        throw new UnsupportedOperationException();
    }
}
