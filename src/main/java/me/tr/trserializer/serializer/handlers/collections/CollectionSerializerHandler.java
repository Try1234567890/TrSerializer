package me.tr.trserializer.serializer.handlers.collections;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.util.Collection;

public class CollectionHandler implements SerializerHandler {
    public static final CollectionHandler INSTANCE = new CollectionHandler();

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(SerializerTask task) {
        if (!(task.getObject() instanceof Collection<?> collection)) return;

        Collection<Object> result = task.getInstancer().instance(collection.getClass());

        for (Object value : collection) {
            task.serialize(value, result::add);
        }

        task.getResult().accept(result);
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Collection;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return me.tr.trserializer.deserializer.handlers.collections.CollectionHandler.INSTANCE;
    }


}