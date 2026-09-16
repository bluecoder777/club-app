package au.jefrin.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse <T> {
    private ApiStatus status;
    private T data;
    private ApiError error;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ApiError {
        private String message;
        private Integer code;
        private Object details;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(ApiStatus.SUCCESS)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return ApiResponse.<T>builder()
                .status(ApiStatus.ERROR)
                .error(ApiError.builder()
                        .code(code)
                        .message(message)
                        .build())
                .build();
    }

    public static <T> ApiResponse<T> error(Integer code, String message, Object details) {
        return ApiResponse.<T>builder()
                .status(ApiStatus.ERROR)
                .error(ApiError.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .build())
                .build();
    }
}

