package me.tr.trserializer.deserializer.handlers.dates;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.dates.CalendarSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.util.Calendar;

public class CalendarDeserializerHandler implements DeserializerHandler {
    public static final CalendarDeserializerHandler INSTANCE = new CalendarDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof Long millis)) return;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        task.getResult().accept(calendar);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(CalendarDeserializerHandler.class) && obj instanceof Long;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return CalendarSerializerHandler.INSTANCE;
    }
}
