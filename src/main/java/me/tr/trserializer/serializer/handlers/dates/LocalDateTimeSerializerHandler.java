package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.dates.LocalDateTimeDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializerHandler implements SerializerHandler {
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    public static final LocalDateTimeSerializerHandler INSTANCE = new LocalDateTimeSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof LocalDateTime localDateTime)) return;

        task.getResult().accept(localDateTime.format(FORMATTER));
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof LocalDateTime;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return LocalDateTimeDeserializerHandler.INSTANCE;
    }
}
