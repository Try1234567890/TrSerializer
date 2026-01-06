package me.tr.trserializer.exceptions;

public class InstancerError extends RuntimeException {

    public InstancerError(String message) {
        super(message);
    }

    public InstancerError(String message, Throwable cause) {
        super(message, cause);
    }
}
