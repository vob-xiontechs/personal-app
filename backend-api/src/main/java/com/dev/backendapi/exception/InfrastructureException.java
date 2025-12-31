package com.dev.backendapi.exception;

public class InfrastructureException extends BaseException {

    public InfrastructureException(String errorCode) {
        super(errorCode, "INFRASTRUCTURE");
    }

    public InfrastructureException(String errorCode, Throwable cause) {
        super(errorCode, "INFRASTRUCTURE", cause);
    }

    public InfrastructureException(String customMessage, String errorCode) {
        super(customMessage, errorCode, "INFRASTRUCTURE");
    }
}
