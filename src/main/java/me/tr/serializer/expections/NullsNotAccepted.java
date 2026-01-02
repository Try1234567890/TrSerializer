package me.tr.serializer.expections;

public class NullsNotAccepted extends RuntimeException {
    public NullsNotAccepted(String message) {
        super(message);
    }
}
