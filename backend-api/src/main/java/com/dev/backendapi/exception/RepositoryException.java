package com.dev.backendapi.exception;

public class RepositoryException extends BaseException {

    public RepositoryException(String message) {
        super(message, "REPOSITORY_ERROR", "REPOSITORY");
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause, "REPOSITORY_ERROR", "REPOSITORY");
    }

    public RepositoryException(String message, String errorCode) {
        super(message, errorCode, "REPOSITORY");
    }
}
