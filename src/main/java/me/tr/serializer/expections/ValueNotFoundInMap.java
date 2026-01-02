package me.tr.serializer.expections;

public class ValueNotFoundInMap extends RuntimeException {
    public ValueNotFoundInMap(String message) {
        super(message);
    }
}
