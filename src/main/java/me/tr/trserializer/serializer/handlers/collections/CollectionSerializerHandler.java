package me.tr.trserializer.serializer.handlers.collections;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.collections.CollectionDeserializerHandler;
import me.tr.trserializer.exceptions.InstancerError;
import me.tr.trserializer.instancer.TranslatorInstancer;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionSerializerHandler implements SerializerHandler {
    public static final CollectionSerializerHandler INSTANCE = new CollectionSerializerHandler();

    @Override
    public void serialize(SerializerTask task) {
        if (!(task.getObject() instanceof Collection<?> collection)) return;
        Collection<Object> result = getCollection(collection, task.getInstancer());

        for (Object value : collection) {
            task.serialize(value, result::add);
        }

        task.getResult().accept(result);
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> getCollection(Collection<?> collection, TranslatorInstancer instancer) {
        try {
            return instancer.instance(collection.getClass());
        } catch (InstancerError ex) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Collection;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return CollectionDeserializerHandler.INSTANCE;
    }


}