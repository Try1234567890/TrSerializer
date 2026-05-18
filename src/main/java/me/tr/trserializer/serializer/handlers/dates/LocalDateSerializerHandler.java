package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.dates.LocalDateDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializerHandler implements SerializerHandler {
    public static final String DEFUALT_FORMAT = "yyyy-MM-dd";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DEFUALT_FORMAT);
    public static final LocalDateSerializerHandler INSTANCE = new LocalDateSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof LocalDate date)) return;

        task.getResult().accept(date.format(FORMATTER));
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof LocalDate;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return LocalDateDeserializerHandler.INSTANCE;
    }
}
