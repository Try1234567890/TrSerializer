package me.tr.serializer.processes.serializer;

import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.processes.Process;
import me.tr.serializer.types.GenericType;
import me.tr.serializer.utility.Three;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class Serializer extends Process {
    private final SerializerOptions options = new SerializerOptions(this);
    private final SerializerCache cache = new SerializerCache(this);

    @Override
    public SerializerOptions getOptions() {
        return options;
    }

    @Override
    public <T> T process(Object o, GenericType<T> type) {
        return serialize(o, type);
    }

    public <T> T serialize(Object obj, Type type) {
        return serialize(obj, new GenericType<>(type));
    }

    public <T> T serialize(Object obj, Class<?> clazz) {
        return serialize(obj, new GenericType<>(clazz));
    }

    public <T> T serialize(Object instance, GenericType<T> type) {
        if (instance == null)
            return null;

        if (!isValid(instance))
            return null;

        Map<String, Object> result = new HashMap<>();

        Optional<T> handler = handlers(instance, type);

        if (handler.isPresent()) {
            return handler.get();
        }

        serialize(result, instance);

        runEndMethods(instance, getOptions().getEndMethods());

        return (T) result;
    }

    public Map<String, Object> serialize(Map<String, Object> result, Object instance) {
        for (Field f : getFields(instance.getClass())) {
            try {
                f.setAccessible(true);

                Object fieldValue = f.get(instance);

                Object value = serialize(fieldValue, f.getType());
                result.put(getMapKey(f), value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Could not serialize field: " + f.getName(), e);
            }
        }
        return result;
    }

    public String getMapKey(Field field) {
        String fieldName = field.getName();
        Class<?> clazz = field.getDeclaringClass();

        return getOptions().getAliases().stream().filter(alias ->
                        (alias.key() == null || alias.key().equals(clazz)) && alias.value().equalsIgnoreCase(fieldName))
                .map(Three::subValue)
                .findFirst().orElse(fieldName);
    }

    private <T> Optional<T> handlers(Object instance, GenericType<T> type) {
        if (getOptions().isUseHandlers()) {
            TypeHandler handler = getHandler(instance);

            if (handler != null) {
                Object serialized = handler.serialize(instance, type);
                if (serialized != null)
                    return (Optional<T>) Optional.of(serialized);
            }
        }
        return Optional.empty();
    }
}
