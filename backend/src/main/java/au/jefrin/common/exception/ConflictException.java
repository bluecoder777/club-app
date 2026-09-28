package au.jefrin.common.exception;

public class ConflictException extends RuntimeException {
    private final String errorKey;

    public ConflictException(String message) {
        this(null, message);
    }

    public ConflictException(String errorKey, String message) {
        super(message);
        this.errorKey = errorKey;
    }

    public String getErrorKey() {
        return errorKey;
    }
}

