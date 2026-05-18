package me.tr.trserializer.serializer.handlers.files;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.files.PathDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.nio.file.Path;

public class PathSerializerHandler implements SerializerHandler {
    public static final PathSerializerHandler INSTANCE = new PathSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof Path path)) return;

        task.getResult().accept(path.toAbsolutePath().normalize());
    }


    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Path;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return PathDeserializerHandler.INSTANCE;
    }
}
