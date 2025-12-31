package com.dev.backendapi.exception;

public class ValidationException extends BaseException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", "VALIDATION");
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause, "VALIDATION_ERROR", "VALIDATION");
    }

    public ValidationException(String message, String errorCode) {
        super(message, errorCode, "VALIDATION");
    }
}
