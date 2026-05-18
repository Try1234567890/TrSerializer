package me.tr.trserializer.deserializer.handlers.files;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.files.BufferedInputStreamSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

public class BufferedInputStreamDeserializerHandler implements DeserializerHandler {
    public static final BufferedInputStreamDeserializerHandler INSTANCE = new BufferedInputStreamDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof byte[] bytes)) return;

        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(bytes));
        task.getResult().accept(bis);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(BufferedInputStream.class) && obj instanceof byte[];
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return BufferedInputStreamSerializerHandler.INSTANCE;
    }
}
