package me.tr.serializer.processes.serializer;

import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.processes.Process;
import me.tr.serializer.types.GenericType;
import me.tr.serializer.utility.Three;

import java.lang.reflect.Field;
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

    public SerializerCache getCache() {
        return cache;
    }


    public Object serialize(Object instance) {
        if (instance == null)
            return null;

        if (!isValid(instance))
            return null;

        if (getCache().has(instance)) {
            return getCache().get(instance);
        }

        Object result = null;

        Optional<Object> handler = handlers(instance);

        if (handler.isPresent())
            result = handler.get();


        if (result != null) {
            getCache().put(instance, result);
            return result;
        }

        result = new HashMap<>();

        getCache().put(instance, result);

        serialize((Map<String, Object>) result, instance);

        runEndMethods(instance, getOptions().getEndMethods());

        return result;
    }

    public Map<String, Object> serialize(Map<String, Object> result, Object instance) {
        for (Field f : getFields(instance.getClass())) {
            try {
                f.setAccessible(true);

                Object value = f.get(instance);

                result.put(getMapKey(f), serialize(value));
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
