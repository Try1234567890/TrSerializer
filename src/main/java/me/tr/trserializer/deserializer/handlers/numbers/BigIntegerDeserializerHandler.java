package me.tr.trserializer.deserializer.handlers.numbers;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.numbers.BigIntegerSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.math.BigInteger;

public class BigIntegerDeserializerHandler implements DeserializerHandler {
    public static final BigIntegerDeserializerHandler INSTANCE = new BigIntegerDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;
        try {
            BigInteger bi = new BigInteger(str);
            task.getResult().accept(bi);
        } catch (NumberFormatException e) {
            throw new HandlerError("An error occurs while trying to deserialize BigInteger", e);
        }


    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object object = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(BigInteger.class) && object instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return BigIntegerSerializerHandler.INSTANCE;
    }
}
