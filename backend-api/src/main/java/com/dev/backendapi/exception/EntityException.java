package com.dev.backendapi.exception;

public class EntityException extends BaseException {

    public EntityException(String errorCode) {
        super(errorCode, "ENTITY");
    }

    public EntityException(String errorCode, Throwable cause) {
        super(errorCode, "ENTITY", cause);
    }

    public EntityException(String customMessage, String errorCode) {
        super(customMessage, errorCode, "ENTITY");
    }
}
