package me.tr.trserializer.processes.serializer;

import me.tr.trserializer.annotations.naming.Naming;
import me.tr.trserializer.annotations.naming.NamingStrategy;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.types.SerializerGenericType;
import me.tr.trserializer.utility.Three;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public class Serializer extends Process {

    public Serializer() {
        setContext(new SerializerContext(this));
    }

    public SerializerContext getContext() {
        return (SerializerContext) super.getContext();
    }

    public SerializerOptions getOptions() {
        return getContext().getOptions();
    }

    public SerializerCache getCache() {
        return getContext().getCache();
    }


    public Object serialize(Object obj) {
        return serialize(obj, Object.class);
    }

    public <T> T serialize(Object obj, T type) {
        if (type == null) {
            TrLogger.exception(new NullPointerException("Object or type is null!"));
            return null;
        }
        return (T) serialize(obj, type.getClass());
    }

    public <T> T serialize(Object obj, Class<T> type) {
        if (type == null) {
            TrLogger.exception(new NullPointerException("Object or type is null!"));
            return null;
        }
        return serialize(obj, new GenericType<>(type));
    }

    public <T> T serialize(Object obj, GenericType<T> type) {
        if (!isValid(obj, type))
            return null;

        if (getCache().has(obj)) {
            TrLogger.dbg("Object (" + obj.getClass().getName() + ") found in cache, reusing it.");
            return (T) getCache().get(obj);
        }

        Optional<T> addons = processAddons(obj, type, null);

        // Already made returns in processAddon()
        // if one of available addons is valid.
        return addons.orElseGet(() -> makeReturn(obj, serializeAsMap(obj), type));

    }


    public Map<String, Object> serializeAsMap(Object obj) {
        Map<String, Object> result = new HashMap<>();

        if (obj == null) {
            TrLogger.exception(new NullPointerException("Object is null!"));
            return result;
        }

        cache(obj, result);

        Class<?> clazz = obj.getClass();
        Set<Field> fields = getFields(clazz);

        for (Field field : fields) {
            field.setAccessible(true);
            String name = getMapKey(field);
            try {
                Object value = field.get(obj);

                if (!isValid(field, value)) {
                    TrLogger.dbg("The validation for field " + name + " in class" + clazz + " failed. Skipping it...");
                    continue;
                }

                GenericType<?> valueType = new SerializerGenericType<>(field);

                Optional<?> addons = processAddons(value, valueType, field);

                if (addons.isPresent()) {
                    result.put(name, addons.get());
                } else result.put(name, serialize(value, valueType));
            } catch (Exception e) {
                TrLogger.exception(new RuntimeException("An error occurs while retrieving value from " + name + " in class " + clazz.getName(), e));
            }
        }

        return result;
    }

    @Override
    protected void cache(Object object, Object result) {
        getCache().put(object, result);
    }

    private String getMapKey(Field field) {
        Class<?> declaringClass = field.getDeclaringClass();
        String fieldName = field.getName();

        for (Three<Class<?>, String, String> three : getOptions().getAliases()) {
            if (three.key().equals(declaringClass) &&
                    three.value().equals(fieldName)) {
                fieldName = three.subValue();
            }
        }

        return applyNamingStrategy(field);
    }

    @Override
    protected Map<Class<?>, String[]> getMethods() {
        return getOptions().getEndMethods();
    }
}

















