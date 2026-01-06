package me.tr.trserializer.processes.deserializer;

import me.tr.trserializer.annotations.Essential;
import me.tr.trserializer.annotations.Unwrapped;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Three;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class Deserializer extends Process {

    public Deserializer() {
        setContext(new DeserializerContext(this));
    }

    @Override
    public DeserializerCache getCache() {
        return (DeserializerCache) super.getCache();
    }

    @Override
    public DeserializerContext getContext() {
        return (DeserializerContext) super.getContext();
    }

    @Override
    public DeserializerOptions getOptions() {
        return (DeserializerOptions) super.getOptions();
    }

    public <T> T deserialize(Object obj, T type) {
        if (type == null) {
            TrLogger.exception(new NullPointerException("Object or type is null!"));
            return null;
        }
        return (T) deserialize(obj, type.getClass());
    }


    public <T> T deserialize(Object obj, Class<T> type) {
        if (type == null) {
            TrLogger.exception(new NullPointerException("Object or type is null!"));
            return null;
        }
        return deserialize(obj, new GenericType<>(type));
    }

    public <T> T deserialize(Object obj, GenericType<T> type) {
        if (!isValid(obj, type))
            return null;

        if (getCache().has(obj)) {
            TrLogger.dbg("Object (" + obj.getClass() + "#" + obj.hashCode() + ") found in cache, reusing it.");
            return (T) getCache().get(obj);
        }


        Optional<Map.Entry<PAddon, ?>> addons = processAddons(obj, type, null);


        // Already made returns in processAddon()
        // if one of available addons is valid.
        if (addons.isPresent()) {
            return (T) addons.get().getValue();
        }

        if (obj instanceof Map<?, ?> map) {
            if (!String.class.equals(Utility.getKeyType(map))) {
                TrLogger.exception(new TypeMissMatched("The keys type of the provided map is not String.class"));
                return null;
            }

            Map<String, Object> checkedMap = (Map<String, Object>) map;
            Object instance = instance(type.getTypeClass(), checkedMap);

            cache(obj, instance);

            return makeReturn(obj, deserializeFromMap(instance, checkedMap), type);
        }

        return makeReturn(obj, instance(type.getTypeClass()), type);
    }


    private Object deserializeFromMap(Object instance, Map<String, Object> map) {
        if (instance == null || map == null) {
            TrLogger.exception(new NullPointerException("Instance or Map is null!"));
            return null;
        }

        Class<?> clazz = instance.getClass();
        Set<Field> fields = getFields(clazz);

        for (Field field : fields) {
            deserializeField(field, instance, map);
        }

        return instance;
    }


    public void deserializeField(Field field, Object instance, Map<String, Object> map) {
        Class<?> clazz = instance.getClass();
        field.setAccessible(true);


        try {
            String fieldName = field.getName();
            Object fieldValue = field.get(instance);

            // Skip fields already set by @Initialize entry point.
            if (fieldValue != null) {
                TrLogger.dbg("Field " + fieldName + " in class " + clazz + " is already set " + fieldValue);
                return;
            }

            GenericType<?> type = new GenericType<>(field);
            Object valueFromMap = getMapValue(field, map);


            Optional<Map.Entry<PAddon, ?>> addons = Optional.empty();
            if (valueFromMap == null &&
                    field.isAnnotationPresent(Unwrapped.class)) {
                addons = processAddons(map, type, field);

            } else if (valueFromMap != null) {
                addons = processAddons(valueFromMap, type, field);
            }

            if (addons.isPresent()) {
                Object addResult = addons.get().getValue();
                setField(field, instance, addResult);
                return;
            }

            Object deserialized = deserialize(valueFromMap, type);
            setField(field, instance, deserialized);
        } catch (IllegalAccessException e) {
            TrLogger.exception(new RuntimeException(
                    "An error occurs while setting value for " + field.getName() + " in class " + clazz, e));
        }
    }

    private void setField(Field field, Object instance, Object value) throws IllegalAccessException {
        Class<?> clazz = instance.getClass();
        if (field.isAnnotationPresent(Essential.class)
                && !isValid(value)) {
            TrLogger.exception(new NullPointerException("The value for field " + field.getName() + " in class " + clazz + " is null and the field is annotated with @Essential."));
        }

        field.set(instance, value);
    }

    private Object getMapValue(Field field, Map<String, Object> map) {
        String fieldName = applyNamingStrategy(field);

        if (map.containsKey(fieldName)) {
            return map.get(fieldName);
        }

        Set<String> aliases = getAliases(field);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (compare(fieldName, key) || aliases.contains(key)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private Set<String> getAliases(Field field) {
        Class<?> declaringClass = field.getDeclaringClass();
        String fieldName = applyNamingStrategy(field);

        for (Three<Class<?>, String, String[]> aliases : getOptions().getAliases()) {
            if (aliases.key().equals(declaringClass) &&
                    compare(fieldName, aliases.value())) {
                return Arrays.stream(aliases.subValue()).collect(Collectors.toSet());
            }
        }

        return new HashSet<>();
    }

    private boolean compare(String s, String s2) {
        return getOptions().isIgnoreCase() ? s.equalsIgnoreCase(s2) : s.equals(s2);
    }

    @Override
    protected Map<Class<?>, String[]> getMethods() {
        return getOptions().getEndMethods();
    }

    @Override
    protected void cache(Object object, Object result) {
        getCache().put(object, result);
    }
}
