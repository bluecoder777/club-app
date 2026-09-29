package au.jefrin.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    private ApiStatus status;
    private T data;
    private ApiError error;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(ApiStatus.SUCCESS)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return error(code, null, message);
    }

    public static <T> ApiResponse<T> error(Integer code, String key, String message) {
        return ApiResponse.<T>builder()
                .status(ApiStatus.ERROR)
                .error(ApiError.builder()
                        .code(code)
                        .key(key)
                        .message(message)
                        .build())
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ApiError {
        private String message;
        private Integer code;
        private String key;
    }

}

