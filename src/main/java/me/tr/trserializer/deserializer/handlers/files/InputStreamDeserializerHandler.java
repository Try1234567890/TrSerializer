package me.tr.trserializer.deserializer.handlers.files;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.files.InputStreamSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class InputStreamDeserializerHandler implements DeserializerHandler {
    public static final InputStreamDeserializerHandler INSTANCE = new InputStreamDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof byte[] bytes)) return;

        InputStream stream = new ByteArrayInputStream(bytes);
        task.getResult().accept(stream);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return (!type.is(BufferedInputStream.class) && type.is(InputStream.class)) && obj instanceof byte[];
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return InputStreamSerializerHandler.INSTANCE;
    }
}
