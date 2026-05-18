package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.dates.ZonedDateTimeDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeSerializerHandler implements SerializerHandler {
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss [VV (O)]";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);

    public static final ZonedDateTimeSerializerHandler INSTANCE = new ZonedDateTimeSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof ZonedDateTime zonedDateTime)) return;

        // TODO: Adding support for @Format annotation.
        task.getResult().accept(zonedDateTime.format(FORMATTER));
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof ZonedDateTime;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return ZonedDateTimeDeserializerHandler.INSTANCE;
    }
}
