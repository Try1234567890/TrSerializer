package me.tr.trserializer.deserializer.handlers.numbers;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.numbers.BigIntegerSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.math.BigDecimal;

public class BigDecimalDeserializerHandler implements DeserializerHandler {
    public static final BigDecimalDeserializerHandler INSTANCE = new BigDecimalDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;
        try {
            BigDecimal bd = new BigDecimal(str);
            task.getResult().accept(bd);
        } catch (NumberFormatException e) {
            throw new HandlerError("An error occurs while trying to deserialize BigDecimal", e);
        }


    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object object = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(BigDecimal.class) && object instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return BigIntegerSerializerHandler.INSTANCE;
    }
}
