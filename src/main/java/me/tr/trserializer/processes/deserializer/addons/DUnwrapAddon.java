package me.tr.trserializer.processes.deserializer.addons;

import me.tr.trserializer.annotations.Unwrapped;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.annotation.UnwrappedHandler;
import me.tr.trserializer.logger.TrLogger;
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

            if (!(obj instanceof Map<?, ?> unsafeMap)) {
                TrLogger.exception(
                        new TypeMissMatched("The provided object is not a map but " + (obj == null ? "null" : obj.getClass())));
                return Optional.empty();
            }

            if (!String.class.isAssignableFrom(Utility.getKeyType(unsafeMap))) {
                TrLogger.exception(
                        new TypeMissMatched("The provided map keys type is not String.class"));
                return Optional.empty();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> values = (Map<String, Object>) unsafeMap;

            Object instance = deserializer.instance(type.getTypeClass(), values);

            return Optional.ofNullable(new UnwrappedHandler(deserializer, getFields(deserializer, unwrapped, obj), instance)
                    .deserialize(values, type));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception {
        return process((Deserializer) process, obj, type, field);
    }
}
