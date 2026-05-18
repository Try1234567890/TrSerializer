package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.dates.CalendarDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.util.Calendar;

public class CalendarSerializerHandler implements SerializerHandler {
    public static final CalendarSerializerHandler INSTANCE = new CalendarSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof Calendar calendar)) return;

        task.getResult().accept(calendar.getTimeInMillis());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Calendar;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return CalendarDeserializerHandler.INSTANCE;
    }
}
