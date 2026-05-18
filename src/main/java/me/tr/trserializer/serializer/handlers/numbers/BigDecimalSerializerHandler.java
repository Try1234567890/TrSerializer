package me.tr.trserializer.serializer.handlers.numbers;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.math.BigDecimal;

public class BigDecimalHandler implements SerializerHandler {
    public static final BigDecimalHandler INSTANCE = new BigDecimalHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof BigDecimal bigDecimal)) return;

        task.getResult().accept(bigDecimal.toPlainString());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof BigDecimal;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return me.tr.trserializer.deserializer.handlers.numbers.BigDecimalHandler.INSTANCE;
    }
}
