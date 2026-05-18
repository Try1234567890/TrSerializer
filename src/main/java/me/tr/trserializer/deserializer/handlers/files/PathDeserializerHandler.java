package me.tr.trserializer.deserializer.handlers.files;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.files.PathSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class PathDeserializerHandler implements DeserializerHandler {
    public static final PathDeserializerHandler INSTANCE = new PathDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;

        try {
            Path path = Path.of(str).normalize();
            task.getResult().accept(path);
        } catch (InvalidPathException ex) {
            throw new HandlerError("An error occurs while parsing path: " + str, ex);
        }

    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(Path.class) && obj instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return PathSerializerHandler.INSTANCE;
    }
}
