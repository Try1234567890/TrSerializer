package me.tr.trserializer.processes.serializer;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.ProcessTaskContainer;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.types.SerializerGenericType;
import me.tr.trserializer.utility.Three;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.*;

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
        return serialize(obj, type, null);
    }

    public Map<String, Object> serializeAsMap(Object obj, Map<String, Object> result) {
        return serializeAsMap(obj, result, null);
    }

    public void serialize(Field field, Object instance,
                          Class<?> clazz, Map<String, Object> result) {
        serialize(field, instance, clazz, result, null);
    }

    public <T> Optional<T> serializeAsSimple(Object obj, GenericType<T> type) {
        if (!isValid(obj, type).isSuccess())
            return Optional.empty();

        if (getCache().has(obj)) {
            TrLogger.dbg("Object (" + Utility.getClassName(obj.getClass()) + ") found in cache, reusing it.");
            return Optional.of((T) getCache().get(obj));
        }

        Optional<Map.Entry<PAddon, ?>> addons = processAddons(obj, type, null);

        // Already made returns in processAddon()
        // if one of available addons is valid.
        if (addons.isPresent()) {
            TrLogger.dbg("Addons found for " + type + ", returning the result.");
            return Optional.of((T) addons.get().getValue());
        }

        return Optional.empty();
    }

    protected <T> T serialize(Object obj, GenericType<T> type, Deque<ProcessTaskContainer> tasks) {
        if (!isValid(obj, type).isSuccess())
            return null;

        Optional<T> simpleResult = serializeAsSimple(obj, type);

        if (simpleResult.isEmpty()) {
            executeEndMethods(obj);
            return makeReturn(obj, serializeAsMap(obj, new HashMap<>(), tasks), type);
        }
        executeEndMethods(obj);
        return simpleResult.get();
    }

    protected Map<String, Object> serializeAsMap(Object obj, Map<String, Object> result, Deque<ProcessTaskContainer> tasks) {
        if (!isValid(obj).isSuccess())
            return result;

        cache(obj, result);

        Class<?> clazz = obj.getClass();
        Set<Field> fields = getFields(clazz);

        for (Field field : fields) {
            serialize(field, obj, clazz, result, tasks);
        }

        return result;
    }

    protected void serialize(Field field, Object instance,
                             Class<?> clazz, Map<String, Object> result,
                             Deque<ProcessTaskContainer> tasks) {
        field.setAccessible(true);
        String fieldName = getMapKey(field);

        TrLogger.dbg("Serializing field " + fieldName + " of " + Utility.getClassName(clazz));

        try {
            Object value = field.get(instance);

            if (!isValid(field, value).isSuccess()) {
                return;
            }

            GenericType<?> type = new SerializerGenericType<>(field);


            Optional<Map.Entry<PAddon, ?>> addons = processAddons(value, type, field);
            if (addons.isPresent()) {
                TrLogger.dbg("Addons found for " + (type.getTypeClass() == Object.class ? Utility.getClassName(clazz) : type) + ", inserting in the result.");

                Map.Entry<PAddon, ?> entry = addons.get();
                Object addonResult = entry.getValue();

                cache(value, addonResult);
                entry.getKey()
                        .getInsert()
                        .insert(fieldName, addonResult, result);
                return;
            }


            TrLogger.dbg("Addons not found for " + type + ", adding to tasks.");
            result(fieldName, value, type, result, tasks).ifPresent(SerResult::accept);
        } catch (Exception e) {
            TrLogger.exception(new RuntimeException("An error occurs while retrieving value from " + fieldName + " in class " + Utility.getClassName(clazz), e));
        }
    }

    protected Optional<? extends RSerResult> result(Object... obj) {
        if (isParamsOfResultInvalid(obj)) {
            TrLogger.exception(new TypeMissMatched("Params for result building are not valid."));
            return Optional.empty();
        }
        if (!(obj[0] instanceof String str)) {
            TrLogger.exception(new TypeMissMatched("The param at index 0 is not the map value key."));
            return Optional.empty();
        }
        if (!(obj[2] instanceof GenericType<?> type)) {
            TrLogger.exception(new TypeMissMatched("The param at index 2 is not the value type."));
            return Optional.empty();
        }
        Object uncheckedResultMap = obj[3];
        if (!Utility.isAMapWithStringKeys(uncheckedResultMap, true)) {
            TrLogger.exception(new TypeMissMatched("The param at index 3 is not the map result."));
            return Optional.empty();
        }
        return Optional.of(new RSerResult(this, str, obj[1], type, (Map<String, Object>) uncheckedResultMap));
    }

    protected String getMapKey(Field field) {
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
    protected Map<Class<?>, String[]> getEndMethods() {
        return getOptions().getEndMethods();
    }

    protected abstract static class SerResult {
        private final Serializer serializer;
        private final String key;
        private final Object value;
        private final GenericType<?> type;

        public SerResult(Serializer serializer, String key, Object value, GenericType<?> type) {
            this.serializer = serializer;
            this.key = key;
            this.value = value;
            this.type = type;
        }

        protected Serializer serializer() {
            return serializer;
        }

        public String key() {
            return key;
        }

        public Object value() {
            return value;
        }

        public GenericType<?> type() {
            return type;
        }

        public abstract void accept();
    }

    protected static class RSerResult extends SerResult {
        private final Map<String, Object> result;

        public RSerResult(Serializer serializer, String key, Object value, GenericType<?> type, Map<String, Object> result) {
            super(serializer, key, value, type);
            this.result = result;
        }

        public Map<String, Object> result() {
            return result;
        }

        @Override
        public void accept() {
            result().put(key(), serializer().serialize(value(), type()));
        }
    }

    protected static class ISerResult extends RSerResult {
        private final Deque<ProcessTaskContainer> tasks;


        public ISerResult(Serializer serializer, String key, Object value, GenericType<?> type, Map<String, Object> result, Deque<ProcessTaskContainer> tasks) {
            super(serializer, key, value, type, result);
            this.tasks = tasks;
        }

        public ISerResult(RSerResult recursiveResult, Deque<ProcessTaskContainer> tasks) {
            super(
                    recursiveResult.serializer(),
                    recursiveResult.key(),
                    recursiveResult.value(),
                    recursiveResult.type(),
                    recursiveResult.result()
            );
            this.tasks = tasks;
        }

        public Deque<ProcessTaskContainer> tasks() {
            return tasks;
        }

        @Override
        public void accept() {
            Optional<?> simpleValue = serializer().serializeAsSimple(value(), type());
            if (simpleValue.isPresent()) {
                result().put(key(), simpleValue.get());
                return;
            }
            Map<String, Object> childMap = new HashMap<>();
            result().put(key(), childMap);
            tasks().push(new ProcessTaskContainer(value(), type(), childMap));
        }
    }
}

















