package com.saber.spring_camel_jdbc.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    private String url;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, String url) {
        super(message);
        this.url = url;
    }

    public ResourceNotFoundException(String message, Throwable cause, String url) {
        super(message, cause);
        this.url = url;
    }

    public ResourceNotFoundException(Throwable cause, String url) {
        super(cause);
        this.url = url;
    }

    public ResourceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String url) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.url = url;
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }

    public ResourceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getUrl() {
        return url;
    }
}
