package me.tr.trserializer.deserializer.handlers.collections;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.CollectionSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionDeserializerHandler implements DeserializerHandler {
    public static final CollectionDeserializerHandler INSTANCE = new CollectionDeserializerHandler();


    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof Collection<?> collection)) return;

        // TODO: Add a way to specify the collection type.  Maybe via @Annotation.
        Collection<Object> result = new ArrayList<>(collection.size());
        GenericType<?> resultType = new GenericType<>(task.getGenericType().getFirstArgumentClass());

        for (Object item : collection) {
            task.deserialize(item, resultType, result::add);
        }

        task.getResult().accept(result);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(Collection.class) && obj instanceof Collection;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return CollectionSerializerHandler.INSTANCE;
    }
}