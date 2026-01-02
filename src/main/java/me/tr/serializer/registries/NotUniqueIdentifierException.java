package me.tr.serializer.registries;

public class NotUniqueIdentifierException extends RuntimeException {
    public NotUniqueIdentifierException(String message) {
        super(message);
    }
}
