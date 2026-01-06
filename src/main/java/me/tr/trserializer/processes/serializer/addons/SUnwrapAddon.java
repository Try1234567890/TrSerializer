package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.annotations.Unwrapped;
import me.tr.trserializer.handlers.annotation.UnwrappedHandler;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.PUnwrapAddon;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public class SUnwrapAddon extends PUnwrapAddon {

    public Optional<Object> process(Serializer serializer, Object obj, GenericType<?> type, Field field)
            throws Exception {
        if (field == null)
            return Optional.empty();

        if (field.isAnnotationPresent(Unwrapped.class)) {
            Unwrapped unwrapped = field.getAnnotation(Unwrapped.class);

            return Optional.ofNullable(new UnwrappedHandler(serializer, getFields(serializer, unwrapped, obj), obj)
                    .serialize(obj, type));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception {
        return process((Serializer) process, obj, type, field);
    }
}
