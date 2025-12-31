package com.dev.backendapi.exception;

public class EntityException extends BaseException {

    public EntityException(String message) {
        super(message, "ENTITY_ERROR", "ENTITY");
    }

    public EntityException(String message, Throwable cause) {
        super(message, cause, "ENTITY_ERROR", "ENTITY");
    }

    public EntityException(String message, String errorCode) {
        super(message, errorCode, "ENTITY");
    }
}
