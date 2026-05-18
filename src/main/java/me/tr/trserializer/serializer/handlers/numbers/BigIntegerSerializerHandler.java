package me.tr.trserializer.serializer.handlers.numbers;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.numbers.BigIntegerDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.math.BigInteger;

public class BigIntegerSerializerHandler implements SerializerHandler {
    public static final BigIntegerSerializerHandler INSTANCE = new BigIntegerSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof BigInteger bigDecimal)) return;

        task.getResult().accept(bigDecimal.toString());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof BigInteger;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return BigIntegerDeserializerHandler.INSTANCE;
    }
}
