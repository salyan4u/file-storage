package ru.solyanin.filestorage.exception;

import java.util.Collection;
import java.util.UUID;

public class BadRequestException extends ServiceException {

    public BadRequestException(String entityName, UUID uuid) {
        super(entityName + " with uuid '" + uuid.toString() + "' not found.");
    }

    public BadRequestException(String entityName, String fieldName, String fieldValue) {
        super("%s with %s '%s' not found.", entityName, fieldName, fieldValue);
    }

    public BadRequestException(String entityName, String pluralFieldName, Collection<String> fieldValues) {
        super("%s with next %s not found: %s.", entityName, pluralFieldName, String.join(", ", fieldValues));
    }

    public BadRequestException(Class<?> entityClass, UUID uuid) {
        super(entityClass + " with uuid '" + uuid.toString() + "' not found.");
    }

    public BadRequestException(Class<?> entityClass, String fieldName, String fieldValue) {
        super("%s with %s '%s' not found.", entityClass, fieldName, fieldValue);
    }

    public BadRequestException(Class<?> entityClass, String pluralFieldName, Collection<String> fieldValues) {
        super("%s with next %s not found: %s.", entityClass, pluralFieldName, String.join(", ", fieldValues));
    }

    public BadRequestException(String message) {
        super(message);
    }
}