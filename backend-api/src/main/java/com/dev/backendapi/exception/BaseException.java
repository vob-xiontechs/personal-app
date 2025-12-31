package com.dev.backendapi.exception;

public abstract class BaseException extends RuntimeException {

    private final String errorCode;
    private final String layer;

    protected BaseException(String errorCode, String layer) {
        super(ErrorMessageProvider.getMessage(layer, errorCode));
        this.errorCode = errorCode;
        this.layer = layer;
    }

    protected BaseException(String errorCode, String layer, Throwable cause) {
        super(ErrorMessageProvider.getMessage(layer, errorCode), cause);
        this.errorCode = errorCode;
        this.layer = layer;
    }

    protected BaseException(String customMessage, String errorCode, String layer) {
        super(customMessage);
        this.errorCode = errorCode;
        this.layer = layer;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getLayer() {
        return layer;
    }

    public String getLayerDescription() {
        return ErrorMessageProvider.getLayerDescription(layer);
    }
}
