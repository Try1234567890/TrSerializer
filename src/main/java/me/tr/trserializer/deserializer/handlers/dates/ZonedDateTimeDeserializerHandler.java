package me.tr.trserializer.deserializer.handlers.dates;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.dates.ZonedDateTimeSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ZonedDateTimeDeserializerHandler implements DeserializerHandler {
    public static final DateTimeFormatter FORMATTER = ZonedDateTimeSerializerHandler.FORMATTER;
    public static final ZonedDateTimeDeserializerHandler INSTANCE = new ZonedDateTimeDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String date)) return;

        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, FORMATTER);
            task.getResult().accept(zonedDateTime);
        } catch (DateTimeParseException ex) {
            throw new HandlerError("An error occurs while parsing ZonedDateTime: " + date, ex);
        }
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(ZonedDateTime.class) && obj instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return ZonedDateTimeSerializerHandler.INSTANCE;
    }
}
