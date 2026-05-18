package me.tr.trserializer.deserializer.handlers.collections;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.MapSerializerHandler;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;
import me.tr.trserializer.utility.Wrappers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MapDeserializerHandler implements DeserializerHandler {
    public static final MapDeserializerHandler INSTANCE = new MapDeserializerHandler();

    @SuppressWarnings("unchecked")
     @Override
     public void deserialize(DeserializerTask task) {
         if (!(task.getObject() instanceof Map<?, ?> map)) return;

         if (areKeyAndValueCorrect(map, task.getGenericType())) {
             task.getResult().accept(map);
             return;
         }

         GenericType<?> type = task.getGenericType();
         // TODO: A way to choose the map type. Maybe with @Annotation.
         Map<Object, Object> result = new HashMap<>();

         for (Map.Entry<?, ?> entry : map.entrySet()) {
             task.deserialize(entry, new GenericType<>(Map.Entry.class, type.getArgumentClass(0), type.getArgumentClass(1)),
                     (o) -> {
                         Map.Entry<Object, Object> subentry = (Map.Entry<Object, Object>) o;
                         if (subentry == null)
                             throw new HandlerError("An error occurs while deserializing Map.Entry: " + entry.getKey() + " - " + entry.getValue());

                         result.put(subentry.getKey(), subentry.getValue());
                     });
         }
         //TODO-FIX: The map is empty at the end
         task.getResult().accept(result);
     }

    private boolean areKeyAndValueCorrect(Map<?, ?> map, GenericType<?> type) {
        Class<?> keyType = type.getArgumentClass(0);
        Class<?> valueType = type.getArgumentClass(1);

        Class<?> keyCls = Utility.getKeyType(map, Object.class);
        Class<?> valueCls = Utility.getValueType(map, Object.class);

        return Wrappers.isAssignable(keyType, keyCls) && Wrappers.isAssignable(valueType, valueCls);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(Map.class) && obj instanceof Map<?, ?>;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return MapSerializerHandler.INSTANCE;
    }


}
