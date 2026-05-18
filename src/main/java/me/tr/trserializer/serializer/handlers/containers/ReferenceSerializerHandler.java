package me.tr.trserializer.serializer.handlers.containers;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.containers.ReferenceDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.utility.SLogger;

import java.lang.ref.Reference;

public class ReferenceSerializerHandler implements SerializerHandler {
    public static final ReferenceSerializerHandler INSTANCE = new ReferenceSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof Reference<?> ref)) return;
        Object obj = ref.get();


        if (obj == null) {
            // TODO: Add option to return null
            SLogger.LOGGER.warn("The value of Reference container is null. Stopping...");
            return;
        }

        task.serialize(obj);
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Reference<?>;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return ReferenceDeserializerHandler.INSTANCE;
    }
}
