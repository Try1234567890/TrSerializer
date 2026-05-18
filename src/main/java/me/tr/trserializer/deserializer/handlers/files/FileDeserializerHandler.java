package me.tr.trserializer.deserializer.handlers.files;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.files.FileSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.io.File;

public class FileDeserializerHandler implements DeserializerHandler {
    public static final FileDeserializerHandler INSTANCE = new FileDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;

        File file = new File(str);
        task.getResult().accept(file);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(File.class) && obj instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return FileSerializerHandler.INSTANCE;
    }
}
