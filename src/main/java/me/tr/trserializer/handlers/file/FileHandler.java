package me.tr.trserializer.handlers.file;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.types.GenericType;

import java.io.File;
import java.nio.file.Path;

public class FileHandler implements TypeHandler {
    @Override
    public Object deserialize(Object obj, GenericType<?> type) {

        if (obj instanceof String str) {
            return new File(str);
        }

        if (obj instanceof Path path) {
            return path.toFile();
        }

        throw new TypeMissMatched("The provided class is not a java.io.File, cannot deserialize it.");
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof File file) {
            return file.getPath();
        }

        throw new TypeMissMatched("The provided class is not a java.io.File, cannot serialize it.");
    }
}
