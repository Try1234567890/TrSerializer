package me.tr.trserializer.processes.serializer;

import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.PAddon;
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

        Optional<Map.Entry<PAddon, ?>> addons = processAddons(obj, type, null);

        // Already made returns in processAddon()
        // if one of available addons is valid.
        if (addons.isPresent()) {
            return (T) addons.get().getValue();
        }

        return makeReturn(obj, serializeAsMap(obj), type);

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
            serializeField(field, obj, result);
        }

        return result;
    }

    public void serializeField(Field field, Object instance, Map<String, Object> result) {
        Class<?> clazz = instance.getClass();
        field.setAccessible(true);
        String name = getMapKey(field);

        try {
            Object value = field.get(instance);

            if (!isValid(field, value)) {
                TrLogger.dbg("The validation for field " + name + " in class" + clazz + " failed. Skipping it...");
                return;
            }

            GenericType<?> valueType = new SerializerGenericType<>(field);

            Optional<Map.Entry<PAddon, ?>> addons = processAddons(value, valueType, field);

            if (addons.isPresent()) {
                Map.Entry<PAddon, ?> entry = addons.get();
                Object addonResult = entry.getValue();

                cache(value, addonResult);

                entry.getKey()
                        .getInsert()
                        .insert(name, addonResult, result);
                return;
            }

            result.put(name, serialize(value, valueType));
        } catch (Exception e) {
            TrLogger.exception(new RuntimeException("An error occurs while retrieving value from " + name + " in class " + clazz.getName(), e));
        }
    }

    @Override
    protected void _cache(Object object, Object result) {
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

















