package me.tr.serializer.expections;

public class TypeMissMatched extends RuntimeException {
    public TypeMissMatched(String message) {
        super(message);
    }

    public TypeMissMatched(Class<?> clazz) {
        this("The keys type of the provided map is not String but " + (clazz != null ? clazz.getName() : "null"));
    }
}
