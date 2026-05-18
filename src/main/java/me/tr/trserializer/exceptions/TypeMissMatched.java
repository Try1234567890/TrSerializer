package me.tr.trserializer.exceptions;

/**
 * Thrown when a process expected type is not respected.
 */
public class TypeMissMatched extends ProcessError {
    public TypeMissMatched(String message) {
        super(message);
    }
}
