package me.tr.trserializer.processes.deserializer;

import me.tr.trserializer.annotations.Essential;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.ProcessAddon;
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
            return makeReturn(getCache().get(obj), obj, type);
        }


        Optional<T> addons = processAddons(obj, type);

        return addons.orElseGet(() -> {
            if (obj instanceof Map<?, ?> map) {
                if (!String.class.equals(Utility.getKeyType(map))) {
                    TrLogger.exception(new TypeMissMatched("The keys type of the provided map is not String.class"));
                    return null;
                }

                Map<String, Object> checkedMap = (Map<String, Object>) map;

                Object instance = instance(type.getTypeClass(), checkedMap);

                Object deserializedInstance = deserializeFromMap(instance, checkedMap);

                return makeReturn(deserializedInstance, obj, type);
            }

            return makeReturn(instance(type.getTypeClass()), obj, type);
        });
    }


    private Object deserializeFromMap(Object instance, Map<String, Object> map) {
        if (instance == null || map == null) {
            TrLogger.exception(new NullPointerException("Instance or Map is null!"));
            return null;
        }

        Class<?> clazz = instance.getClass();
        Set<Field> fields = getFields(clazz);

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                // Skip fields already set by @Initialize method.
                if (field.get(instance) != null)
                    continue;


                Object valueFromMap = getMapValue(field, map);
                Object deserialized = deserialize(valueFromMap, new GenericType<>(field));

                if (field.isAnnotationPresent(Essential.class)
                        && !isValid(deserialized)) {
                    TrLogger.exception(new NullPointerException("The value for field " + field.getName() + " in class " + clazz + " is null and the field is annotated with @Essential."));
                }

                field.set(instance, deserialized);
            } catch (IllegalAccessException e) {
                TrLogger.exception(new RuntimeException(
                        "An error occurs while setting value for " + field.getName() + " in class " + clazz, e));
            }
        }

        return instance;
    }

    private Object getMapValue(Field field, Map<String, Object> map) {
        String fieldName = field.getName();

        if (map.containsKey(fieldName)) {
            return map.get(fieldName);
        }

        Set<String> aliases = getAliases(field);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (compare(key, fieldName)
                    || aliases.contains(key)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private Set<String> getAliases(Field field) {
        Class<?> declaringClass = field.getDeclaringClass();
        String fieldName = field.getName();


        for (Three<Class<?>, String, String[]> aliases : getOptions().getAliases()) {
            if (aliases.key().equals(declaringClass) &&
                    compare(aliases.value(), fieldName)) {
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
}
