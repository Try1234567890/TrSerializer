package me.tr.trserializer.deserializer.handlers.dates;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.dates.LocalDateTimeSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializerHandler implements DeserializerHandler {
    public static final DateTimeFormatter FORMATTER = LocalDateTimeSerializerHandler.FORMATTER;
    public static final LocalDateTimeDeserializerHandler INSTANCE = new LocalDateTimeDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;

        LocalDateTime localDateTime = LocalDateTime.parse(str, FORMATTER);
        task.getResult().accept(localDateTime);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(LocalDateTime.class) && obj instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return LocalDateTimeSerializerHandler.INSTANCE;
    }
}
