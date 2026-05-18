package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeHandler implements SerializerHandler {
    public static final String DEFAULT_FORMAT = "uuuu-MM-dd'T'HH:mmXXXXX";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    public static final OffsetDateTimeHandler INSTANCE = new OffsetDateTimeHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof OffsetDateTime offsetDateTime)) return;

        // TODO: Adding support for @Format annotation.
        task.getResult().accept(offsetDateTime.format(FORMATTER));
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof OffsetDateTime;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return me.tr.trserializer.deserializer.handlers.dates.OffsetDateTimeHandler.INSTANCE;
    }
}
