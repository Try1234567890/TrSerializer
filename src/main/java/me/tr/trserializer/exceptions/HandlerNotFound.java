package me.tr.trserializer.exceptions;

public class HandlerNotFound extends RuntimeException {
    public HandlerNotFound(String message) {
        super(message);
    }
}
