package com.dev.backendapi.exception;

public class RepositoryException extends BaseException {

    public RepositoryException(String errorCode) {
        super(errorCode, "REPOSITORY");
    }

    public RepositoryException(String errorCode, Throwable cause) {
        super(errorCode, "REPOSITORY", cause);
    }

    public RepositoryException(String customMessage, String errorCode) {
        super(customMessage, errorCode, "REPOSITORY");
    }
}
