package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.annotations.wrap.Wrapped;
import me.tr.trserializer.handlers.annotation.WrappedHandler;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.PWrapAddon;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;

public class SWrapAddon extends PWrapAddon {

    public Optional<Object> process(Serializer serializer, Object obj, GenericType<?> type, Field field)
            throws Exception {
        if (field == null)
            return Optional.empty();

        if (field.isAnnotationPresent(Wrapped.class)) {
            Wrapped unwrapped = field.getAnnotation(Wrapped.class);

            return Optional.ofNullable(
                    new WrappedHandler(serializer, unwrapped.key(), new HashSet<>(), obj)
                            .serialize(obj, type)
            );
        }

        return Optional.empty();
    }

    @Override
    public Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception {
        return process((Serializer) process, obj, type, field);
    }
}
