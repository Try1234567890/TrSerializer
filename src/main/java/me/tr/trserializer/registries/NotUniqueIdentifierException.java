package me.tr.trserializer.registries;

public class NotUniqueIdentifierException extends RuntimeException {
    public NotUniqueIdentifierException(String message) {
        super(message);
    }
}
