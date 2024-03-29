package ru.solyanin.filestorage.exception;

public class ServiceException extends Exception {
    protected ServiceException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}