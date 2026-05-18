package me.tr.trserializer.deserializer.handlers.containers;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.containers.ReferenceSerializerHandler;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Wrappers;

import java.lang.ref.Reference;
import java.util.Map;

public class ReferenceDeserializerHandler implements DeserializerHandler {
    public static final ReferenceDeserializerHandler INSTANCE = new ReferenceDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        Object rawObject = task.getObject();
        Class<?> exceptedObjectType = task.getGenericType().getFirstArgumentClass();

        if (rawObject == null) {
            Reference<?> reference = (Reference<?>) task.instance();
            task.getResult().accept(reference);
            return;
        }

        if (Wrappers.isAssignable(rawObject.getClass(), exceptedObjectType)) {
            //noinspection DataFlowIssue
            Reference<?> reference = (Reference<?>) task.getInstancer().instance(exceptedObjectType, Map.ofEntries(
                    Map.entry("referent", rawObject),
                    Map.entry("q", null)
            ));
            task.getResult().accept(reference);
            return;
        }

        task.deserialize(rawObject, new GenericType<>(exceptedObjectType), (o) -> task.deserialize(o, task.getGenericType(), task.getResult().getConsumer()));
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        GenericType<?> type = task.getGenericType();

        return type.is(Reference.class);
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return ReferenceSerializerHandler.INSTANCE;
    }
}
