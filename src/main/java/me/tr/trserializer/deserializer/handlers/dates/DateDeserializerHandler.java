package me.tr.trserializer.deserializer.handlers.dates;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.dates.DateSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.util.Date;

public class DateDeserializerHandler implements DeserializerHandler {
    public static final DateDeserializerHandler INSTANCE = new DateDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof Long millis)) return;

        Date date = new Date(millis);
        task.getResult().accept(date);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(Date.class) && obj instanceof Long;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return DateSerializerHandler.INSTANCE;
    }
}
