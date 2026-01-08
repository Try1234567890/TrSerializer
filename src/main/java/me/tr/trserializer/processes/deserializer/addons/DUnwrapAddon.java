package me.tr.trserializer.processes.deserializer.addons;

import me.tr.trserializer.annotations.unwrap.Unwrapped;
import me.tr.trserializer.handlers.annotation.UnwrappedHandler;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.PUnwrapAddon;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

public class DUnwrapAddon extends PUnwrapAddon {

    public Optional<Object> process(Deserializer deserializer, Object obj, GenericType<?> type, Field field)
            throws Exception {
        if (field == null)
            return Optional.empty();

        if (field.isAnnotationPresent(Unwrapped.class)) {
            Unwrapped unwrapped = field.getAnnotation(Unwrapped.class);

            if (!Utility.isAMapWithStringKeys(obj)) {
                return Optional.empty();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> values = (Map<String, Object>) obj;

            Object instance = deserializer.instance(type.getTypeClass(), values);

            return Optional.ofNullable(new UnwrappedHandler(deserializer, getFields(deserializer, unwrapped, instance), instance)
                    .deserialize(values, type));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception {
        return process((Deserializer) process, obj, type, field);
    }
}
