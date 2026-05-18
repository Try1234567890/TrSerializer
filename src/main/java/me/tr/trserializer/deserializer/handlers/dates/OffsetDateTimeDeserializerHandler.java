package me.tr.trserializer.deserializer.handlers.dates;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.dates.OffsetDateTimeSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeDeserializerHandler implements DeserializerHandler {
    public static final DateTimeFormatter FORMATTER = OffsetDateTimeSerializerHandler.FORMATTER;
    public static final OffsetDateTimeDeserializerHandler INSTANCE = new OffsetDateTimeDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;

        OffsetDateTime offsetDateTime = OffsetDateTime.parse(str, FORMATTER);
        task.getResult().accept(offsetDateTime);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(OffsetDateTime.class) && obj instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return OffsetDateTimeSerializerHandler.INSTANCE;
    }
}
