package me.tr.serializer.exceptions;

public class NullsNotAccepted extends RuntimeException {
    public NullsNotAccepted(String message) {
        super(message);
    }
}
