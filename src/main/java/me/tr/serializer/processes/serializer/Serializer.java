package me.tr.serializer.processes.serializer;

import me.tr.serializer.exceptions.TypeMissMatched;
import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.logger.TrLogger;
import me.tr.serializer.processes.Process;
import me.tr.serializer.types.GenericType;
import me.tr.serializer.utility.Three;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("unchecked")
public class Serializer extends Process {
    private final SerializerOptions options = new SerializerOptions(this);
    private final SerializerCache cache = new SerializerCache(this);

    @Override
    public SerializerOptions getOptions() {
        return options;
    }

    public SerializerCache getCache() {
        return cache;
    }


    public Object serialize(Object instance) {
        return serialize(instance, Object.class);
    }

    public <T> T serialize(Object instance, T type) {
        return serialize(instance, new GenericType<>(type == null ? Object.class : type.getClass()));
    }

    public <T> T serialize(Object instance, Class<T> type) {
        return serialize(instance, new GenericType<>(type));
    }

    public <T> T serialize(Object instance, GenericType<T> type) {
        if (instance == null)
            return null;

        if (!isValid(instance))
            return null;

        if (getCache().has(instance)) {
            return checkReturn(getCache().get(instance), type);
        }

        Object result = null;

        Optional<Object> handler = handlers(instance);

        if (handler.isPresent())
            result = handler.get();


        if (result != null) {
            getCache().put(instance, result);
            return checkReturn(result, type);
        }

        result = new HashMap<>();

        getCache().put(instance, result);

        serialize((Map<String, Object>) result, instance);

        runEndMethods(instance, getOptions().getEndMethods());

        return checkReturn(result, type);
    }

    public Map<String, Object> serializeComplex(Object instance) {
        Map<String, Object> result = new HashMap<>();
        serialize(result, instance);
        return result;
    }

    private void serialize(Map<String, Object> result,
                           Object instance) {
        for (Field f : getFields(instance.getClass())) {
            try {
                f.setAccessible(true);

                Object value = f.get(instance);

                result.put(getMapKey(f), serialize(value));
            } catch (IllegalAccessException e) {
                TrLogger.getInstance().exception(
                        new RuntimeException("Could not serialize field: " + f.getName(), e));
            }
        }
    }

    private String getMapKey(Field field) {
        String fieldName = field.getName();
        Class<?> clazz = field.getDeclaringClass();

        return getOptions().getAliases().stream().filter(alias ->
                        (alias.key() == null || alias.key().equals(clazz))
                                && alias.value().equalsIgnoreCase(fieldName))
                .map(Three::subValue)
                .findFirst().orElse(fieldName);
    }

    private Optional<Object> handlers(Object instance) {
        if (getOptions().isUseHandlers()) {
            TypeHandler handler = getHandler(instance);

            if (handler != null) {
                Object serialized = handler.serialize(instance, new GenericType<>(instance.getClass()));
                if (serialized != null)
                    return Optional.of(serialized);
            }
        }

        return Optional.empty();
    }
}
