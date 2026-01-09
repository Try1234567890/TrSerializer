package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.annotations.unwrap.Unwrap;
import me.tr.trserializer.annotations.unwrap.Unwrapped;
import me.tr.trserializer.handlers.annotation.UnwrappedHandler;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.PAddonsUtility;
import me.tr.trserializer.processes.process.addons.Priority;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.processes.serializer.helper.insert.InsertMethod;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SUnwrapAddon extends SAddon {

    public SUnwrapAddon() {
        super("wrapper", Priority.HIGH, InsertMethod.UNWRAP);
    }

    public Optional<Object> process(Serializer serializer, Object obj, GenericType<?> type, Field field)
            throws Exception {
        if (field == null)
            return Optional.empty();

        if (field.isAnnotationPresent(Unwrapped.class)) {
            Unwrapped unwrapped = field.getAnnotation(Unwrapped.class);

            return Optional.ofNullable(new UnwrappedHandler(serializer, PAddonsUtility.getFields(serializer, unwrapped, obj), obj)
                    .serialize(obj, type));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception {
        return process((Serializer) process, obj, type, field);
    }
}
