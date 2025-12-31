package com.dev.backendapi.exception;

public class InfrastructureException extends BaseException {

    public InfrastructureException(String message) {
        super(message, "INFRASTRUCTURE_ERROR", "INFRASTRUCTURE");
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause, "INFRASTRUCTURE_ERROR", "INFRASTRUCTURE");
    }

    public InfrastructureException(String message, String errorCode) {
        super(message, errorCode, "INFRASTRUCTURE");
    }
}
