package com.saber.spring_camel_jdbc.exceptions;

public class ResourceExistException extends RuntimeException{
    private String url;

    public ResourceExistException() {
    }

    public ResourceExistException(String message) {
        super(message);
    }

    public ResourceExistException(String message, String url) {
        super(message);
        this.url = url;
    }

    public ResourceExistException(String message, Throwable cause, String url) {
        super(message, cause);
        this.url = url;
    }

    public ResourceExistException(Throwable cause, String url) {
        super(cause);
        this.url = url;
    }

    public ResourceExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String url) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.url = url;
    }

    public ResourceExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceExistException(Throwable cause) {
        super(cause);
    }

    public ResourceExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getUrl() {
        return url;
    }
}
