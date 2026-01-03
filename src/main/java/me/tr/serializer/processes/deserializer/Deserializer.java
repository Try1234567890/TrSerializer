package me.tr.serializer.processes.deserializer;

import me.tr.serializer.annotations.Essential;
import me.tr.serializer.exceptions.TypeMissMatched;
import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.logger.TrLogger;
import me.tr.serializer.processes.Process;
import me.tr.serializer.registries.ConvertersRegistry;
import me.tr.serializer.types.GenericType;
import me.tr.serializer.utility.Three;
import me.tr.serializer.utility.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class Deserializer extends Process {
    private final DeserializerOptions options = new DeserializerOptions(this);
    private final DeserializerCache cache = new DeserializerCache(this);


    @Override
    public DeserializerOptions getOptions() {
        return options;
    }

    public DeserializerCache getCache() {
        return cache;
    }

    public <T> T deserialize(Object instance, T type) {
        return deserialize(instance, new GenericType<>(type == null ? Object.class : type.getClass()));
    }

    public <T> T deserialize(Object obj, Class<T> clazz) {
        return deserialize(obj, new GenericType<>(clazz));
    }

    public <T> T deserialize(Object obj, GenericType<T> type) {
        if (!isValid(obj)) {
            TrLogger.getInstance().error("The object is invalid.");
            return null;
        }

        if (type == null) {
            TrLogger.getInstance().exception(
                    new NullPointerException("The type is null, cannot deserialize map."));
            return null;
        }

        if (getCache().has(obj)) {
            return checkReturn(getCache().get(obj), type);
        }

        T instance = null;

        Optional<T> alternative = alternative(obj, type);
        if (alternative.isPresent())
            instance = alternative.get();


        Optional<T> handler = handlers(obj, type);

        if (handler.isPresent())
            instance = handler.get();

        Class<?> clazz = type.getTypeClass();

        if (instance != null) {
            getCache().put(obj, instance);
            return instance;
        }

        instance = (T) instance(clazz);

        getCache().put(obj, instance);

        if (!(obj instanceof Map<?, ?> map)) {
            if (clazz.isInstance(obj) || clazz.isPrimitive())
                return (T) obj;
            TrLogger.getInstance().exception(new TypeMissMatched("Expected Map for object deserialization, but got: " + obj.getClass().getName()));
            return null;
        }

        Class<?> keysType = Utility.getKeyType(map);
        if (!String.class.equals(keysType)) {
            TrLogger.getInstance().exception(
                    new TypeMissMatched("The expected keys type for complex object map is String. Found: " + (keysType == null ? "null (is empty: " + map.isEmpty() + ")" : keysType.getName())));
            return null;
        }

        deserialize((Map<String, Object>) map, instance);

        runEndMethods(instance, getOptions().getEndMethods());

        return instance;
    }

    private void deserialize(Map<String, Object> map, Object instance) {
        Class<?> clazz = instance.getClass();

        for (Field field : getFields(clazz)) {
            String fieldName = field.getName();
            Object mapValue = getMapValue(map, field);

            if (mapValue == null) {
                if (field.isAnnotationPresent(Essential.class)) {
                    TrLogger.getInstance().exception(
                            new NullPointerException("The value " + fieldName + " is essential but missing."));
                }
                continue;
            }

            try {
                GenericType<?> fieldType = new GenericType<>(field);

                Object value = deserialize(mapValue, fieldType);

                field.setAccessible(true);
                setField(instance, field, value);
            } catch (Exception e) {
                TrLogger.getInstance().exception(
                        new RuntimeException("Error assigning field " + fieldName + " in " + clazz.getSimpleName(), e));
            }
        }
    }

    private void setField(Object instance, Field field, Object value) throws IllegalAccessException {
        Class<?> type = field.getType();

        value = checkReturn(value, new GenericType<>(type));

        if (value == null) {
            if (type.isPrimitive()) return;
            field.set(instance, null);
            return;
        }

        if (getOptions().isUseNumericBoolean()
                && Number.class.isAssignableFrom(value.getClass())
                && Boolean.class.isAssignableFrom(type)) {
            field.set(instance, ConvertersRegistry.getBooleanConverter().complex(((Number) value).byteValue()));
            return;
        }

        if (getOptions().isUseNumericCharacter()
                && Number.class.isAssignableFrom(value.getClass())
                && Character.class.isAssignableFrom(type)) {
            field.set(instance, ConvertersRegistry.getCharacterConverter().complex(((Number) value).intValue()));
            return;
        }

        field.set(instance, value);
    }

    private <T> Optional<T> alternative(Object obj, GenericType<T> type) {
        Class<?> clazz = type.getTypeClass();

        if (getOptions().hasAlternatives(clazz)) {
            Function<Object, Class<?>> alternative = getOptions().getAlternatives(clazz);

            if (alternative != null
                    && !alternative.apply(obj).equals(clazz))
                return Optional.ofNullable(checkReturn(deserialize(obj, alternative), type));

        }

        return Optional.empty();
    }

    private <T> Optional<T> handlers(Object obj, GenericType<T> type) {
        Class<?> clazz = type.getTypeClass();

        if (getOptions().isUseHandlers()) {
            TypeHandler handler = getHandler(clazz);

            if (handler != null)
                return Optional.ofNullable(checkReturn(handler.deserialize(obj, type), type));
        }

        return Optional.empty();
    }

    private Object getMapValue(Map<?, ?> map, Field field) {
        String fieldName = field.getName();
        Class<?> clazz = field.getDeclaringClass();

        Object mapValue = map.get(fieldName);

        if (mapValue == null) {
            List<String> aliases = List.of(getOptions().getAliases()
                    .stream()
                    .filter(alias -> (alias.key() == null || alias.key().equals(clazz)) && compare(alias.value(), fieldName))
                    .map(Three::subValue)
                    .findFirst()
                    .orElse(new String[0]));

            mapValue = map.entrySet()
                    .stream()
                    .filter(entry -> {
                        String key = String.valueOf(entry.getKey());
                        return compare(key, fieldName) || aliases.contains(key);
                    })
                    .map(Map.Entry::getValue)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }

        return mapValue;
    }

    private boolean compare(String first, String second) {
        return getOptions().isIgnoreCase() ? first.equalsIgnoreCase(second) : first.equals(second);
    }
}
