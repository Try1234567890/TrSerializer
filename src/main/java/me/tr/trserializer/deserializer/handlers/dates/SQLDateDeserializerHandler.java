package me.tr.trserializer.deserializer.handlers.dates;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.dates.SQLDateSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.sql.Date;

public class SQLDateDeserializerHandler implements DeserializerHandler {
    public static final SQLDateDeserializerHandler INSTANCE = new SQLDateDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof Long millis)) return;

        Date sqlDate = new Date(millis);
        task.getResult().accept(sqlDate);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(Date.class) && obj instanceof Long;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return SQLDateSerializerHandler.INSTANCE;
    }
}
