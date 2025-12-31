package com.dev.backendapi.exception;

public class ValidationException extends BaseException {

    public ValidationException(String errorCode) {
        super(errorCode, "VALIDATION");
    }

    public ValidationException(String errorCode, Throwable cause) {
        super(errorCode, "VALIDATION", cause);
    }

    public ValidationException(String customMessage, String errorCode) {
        super(customMessage, errorCode, "VALIDATION");
    }
}
