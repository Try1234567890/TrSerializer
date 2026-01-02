package me.tr.serializer.expections;

public class HandlerNotFound extends RuntimeException {
    public HandlerNotFound(String message) {
        super(message);
    }
}
