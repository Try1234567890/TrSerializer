package me.tr.trserializer.deserializer.handlers.dates;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.dates.LocalDateSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializerHandler implements DeserializerHandler {
    public static final DateTimeFormatter FORMATTER = LocalDateSerializerHandler.FORMATTER;
    public static final LocalDateDeserializerHandler INSTANCE = new LocalDateDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;

        LocalDate date = LocalDate.parse(str, FORMATTER);
        task.getResult().accept(date);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(LocalDate.class) && obj instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return LocalDateSerializerHandler.INSTANCE;
    }
}
