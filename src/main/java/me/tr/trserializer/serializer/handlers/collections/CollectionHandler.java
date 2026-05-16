package me.tr.trserializer.serializer.handlers.collections;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionHandler implements SerializerHandler {
    public static final CollectionHandler INSTANCE = new CollectionHandler();

    @Override
    public void serialize(SerializerTask task) {
        if (!(task.getObject() instanceof Collection<?> collection)) return;

        // TODO: Add clone(Collection) methods when instancer is finished.
        //       Clone the initial collection for coherence.
        List<Object> result = new ArrayList<>();

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
        return null;
    }


}