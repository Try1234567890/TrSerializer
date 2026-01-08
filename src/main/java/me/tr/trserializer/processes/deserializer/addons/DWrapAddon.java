package me.tr.trserializer.processes.deserializer.addons;

import me.tr.trserializer.annotations.wrap.Wrapped;
import me.tr.trserializer.handlers.annotation.WrappedHandler;
import me.tr.trserializer.logger.ProcessLogger;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.PWrapAddon;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

public class DWrapAddon extends PWrapAddon {

    public Optional<Object> process(Deserializer deserializer, Object obj, GenericType<?> type, Field field)
            throws Exception {
        if (field == null)
            return Optional.empty();

        if (field.isAnnotationPresent(Wrapped.class)) {
            Wrapped wrapped = field.getAnnotation(Wrapped.class);

            if (!Utility.isAMapWithStringKeys(obj)) {
                deserializer.getLogger().debug("Cannot proceed, the provided object is not valid.");
                return Optional.empty();
            }

            Object subMap = ((Map<?, ?>) obj).get(wrapped.key());

            if (!Utility.isAMapWithStringKeys(subMap)) {
                deserializer.getLogger().debug("Cannot proceed, the found object found as sub map is not valid.");
                return Optional.empty();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> values = (Map<String, Object>) subMap;

            Object instance = deserializer.instance(type.getTypeClass(), values);

            return Optional.ofNullable(new WrappedHandler(deserializer, wrapped.key(), deserializer.getFields(field.getDeclaringClass()), instance)
                    .deserialize(values, type));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception {
        return process((Deserializer) process, obj, type, field);
    }
}
