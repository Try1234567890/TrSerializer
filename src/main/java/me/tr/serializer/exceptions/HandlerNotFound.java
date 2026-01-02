package me.tr.serializer.exceptions;

public class HandlerNotFound extends RuntimeException {
    public HandlerNotFound(String message) {
        super(message);
    }
}
