package ru.solyanin.filestorage.exception;

public class InternalErrorException extends ServiceException {
    public InternalErrorException(String message) {
        super(message);
    }
}