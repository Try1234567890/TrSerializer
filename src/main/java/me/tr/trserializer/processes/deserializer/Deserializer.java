package me.tr.trserializer.processes.deserializer;

import me.tr.trserializer.annotations.unwrap.Unwrapped;
import me.tr.trserializer.annotations.wrap.Wrapped;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.logger.ProcessLogger;
import me.tr.trserializer.processes.deserializer.helper.DValueRetriever;
import me.tr.trserializer.processes.deserializer.helper.DValueSetter;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.ProcessTaskContainer;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.*;

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

    public DValueRetriever getValueRetriever() {
        return getContext().getValueRetriever();
    }

    public DValueSetter getValueSetter() {
        return getContext().getValueSetter();
    }


    public <T> T deserialize(Object obj, T type) {
        if (type == null) {
            getLogger().throwable(new NullPointerException("Object or type is null!"));
            return null;
        }
        return (T) deserialize(obj, type.getClass());
    }

    public <T> T deserialize(Object obj, Class<T> type) {
        if (type == null) {
            getLogger().throwable(new NullPointerException("Object or type is null!"));
            return null;
        }
        return deserialize(obj, new GenericType<>(type));
    }

    protected <T> Optional<T> deserializeAsSimple(Object obj, GenericType<T> type) {
        if (!getProcessValidator().isValid(obj, type).isSuccess())
            return Optional.empty();

        getMethodsExecutor().executeStartMethods(obj);

        if (getCache().has(obj)) {
            ProcessLogger.dbg("Object (" + obj.getClass() + "#" + obj.hashCode() + ") found in cache, reusing it.");
            return Optional.of((T) getCache().get(obj));
        }


        Optional<Map.Entry<PAddon, ?>> addons = processAddons(obj, type, null);


        // Already made returns in processAddon()
        // if one of available addons is valid.
        if (addons.isPresent()) {
            return Optional.of((T) addons.get().getValue());
        }

        getMethodsExecutor().executeEndMethods(obj);

        return Optional.empty();
    }

    public <T> T deserialize(Object obj, GenericType<T> type) {
        return deserialize(obj, type, null);
    }

    public Object deserializeFromMap(Object obj, Map<String, Object> map) {
        return deserializeFromMap(obj, map, null);
    }

    public void deserialize(Field field, Object obj,
                            Class<?> clazz, Map<String, Object> map) {
        deserialize(field, obj, clazz, map, null);
    }

    protected <T> T deserialize(Object obj, GenericType<T> type,
                                Deque<ProcessTaskContainer> tasks) {
        if (!getProcessValidator().isValid(obj, type).isSuccess())
            return null;

        Optional<T> simpleResult = deserializeAsSimple(obj, type);

        if (simpleResult.isEmpty()) {
            if (!Utility.isAMapWithStringKeys(obj)) {
                ProcessLogger.dbg("The provided object is not a map. Cannot set fields values for " + type + ", an empty instance will be used.");
                return makeReturn(obj, instance(type.getTypeClass()), type);
            }

            Map<String, Object> checkedMap = (Map<String, Object>) obj;
            Object instance = instance(type.getTypeClass(), checkedMap);

            cache(obj, instance);


            return makeReturn(obj, deserializeFromMap(instance, checkedMap, tasks), type);
        }

        return simpleResult.get();
    }


    protected Object deserializeFromMap(Object obj, Map<String, Object> map,
                                        Deque<ProcessTaskContainer> tasks) {
        if (obj == null || map == null) {
            getLogger().throwable(new NullPointerException("Object or Map is null!"));
            return null;
        }

        getMethodsExecutor().executeStartMethods(obj);

        Class<?> clazz = obj.getClass();
        Set<Field> fields = getFields(clazz);

        for (Field field : fields) {
            deserialize(field, obj, clazz, map, tasks);
        }

        getMethodsExecutor().executeEndMethods(obj);

        return obj;
    }


    protected void deserialize(Field field, Object obj,
                               Class<?> clazz, Map<String, Object> values,
                               Deque<ProcessTaskContainer> tasks) {
        field.setAccessible(true);


        try {
            String fieldName = field.getName();
            Object fieldValue = field.get(obj);

            // Skip fields already set by @Initialize entry point.
            if (fieldValue != null) {
                ProcessLogger.dbg("Field " + fieldName + " in class " + Utility.getClassName(clazz) + " is already set " + fieldValue);
                return;
            }

            if (!getProcessValidator().isValid(field).isSuccess())
                return;

            GenericType<?> type = new GenericType<>(field);
            Object valueFromMap = getValueRetriever().getMapValue(field, values);


            Optional<Map.Entry<PAddon, ?>> addons = Optional.empty();
            if (valueFromMap == null &&
                    (field.isAnnotationPresent(Unwrapped.class) ||
                            field.isAnnotationPresent(Wrapped.class))) {
                addons = processAddons(values, type, field);

            } else if (valueFromMap != null) {
                addons = processAddons(valueFromMap, type, field);
            }

            if (addons.isPresent()) {
                Object addResult = addons.get().getValue();
                getValueSetter().setField(field, obj, addResult);
                return;
            }

            result(field, valueFromMap, obj, type, values, tasks).ifPresent(DesResult::accept);
        } catch (IllegalAccessException e) {
            getLogger().throwable(new RuntimeException("An error occurs while setting value for " + field.getName() + " in class " + Utility.getClassName(clazz), e));
        }
    }

    protected Optional<? extends RDesResult> result(Object... obj) {
        if (isParamsOfResultInvalid(obj)) {
            getLogger().throwable(new TypeMissMatched("Params for result building are not valid."));
            return Optional.empty();
        }
        if (!(obj[0] instanceof Field field)) {
            getLogger().throwable(new TypeMissMatched("The param at index 0 is not the processing field."));
            return Optional.empty();
        }
        if (!(obj[3] instanceof GenericType<?> type)) {
            getLogger().throwable(new TypeMissMatched("The param at index 3 is not the value type."));
            return Optional.empty();
        }
        Object uncheckedResultMap = obj[4];
        if (!Utility.isAMapWithStringKeys(uncheckedResultMap, true)) {
            getLogger().throwable(new TypeMissMatched("The param at index 3 is not the map result."));
            return Optional.empty();
        }
        return Optional.of(new RDesResult(this, field, obj[1], obj[2], type, (Map<String, Object>) uncheckedResultMap));
    }

    protected abstract static class DesResult {
        private final Deserializer deserializer;
        private final Field field;
        private final Object instance;
        private final Object mapValue;
        private final GenericType<?> type;
        private final Map<String, Object> values;

        public DesResult(Deserializer deserializer, Field field, Object mapValue, Object instance, GenericType<?> type, Map<String, Object> values) {
            this.deserializer = deserializer;
            this.field = field;
            this.instance = instance;
            this.mapValue = mapValue;
            this.type = type;
            this.values = values;
        }

        protected Deserializer deserializer() {
            return deserializer;
        }

        public Field field() {
            return field;
        }

        public Object instance() {
            return instance;
        }

        public Object mapValue() {
            return mapValue;
        }

        public GenericType<?> type() {
            return type;
        }

        public Map<String, Object> values() {
            return values;
        }

        public abstract void accept();
    }

    protected static class RDesResult extends DesResult {

        public RDesResult(Deserializer deserializer, Field field, Object mapValue, Object instance, GenericType<?> type, Map<String, Object> map) {
            super(deserializer, field, mapValue, instance, type, map);
        }

        @Override
        public void accept() {
            Object deserialized = deserializer().deserialize(mapValue(), type());

            deserializer().getValueSetter().setField(field(), instance(), deserialized);
        }
    }

    protected static class IDesResult extends RDesResult {
        private final Deque<ProcessTaskContainer> tasks;

        public IDesResult(RDesResult recursiveResult, Deque<ProcessTaskContainer> tasks) {
            super(
                    recursiveResult.deserializer(),
                    recursiveResult.field(),
                    recursiveResult.mapValue(),
                    recursiveResult.instance(),
                    recursiveResult.type(),
                    recursiveResult.values()
            );
            this.tasks = tasks;
        }

        public Deque<ProcessTaskContainer> tasks() {
            return tasks;
        }

        @Override
        public void accept() {
            Optional<?> simpleValue = deserializer().deserializeAsSimple(mapValue(), type());
            if (simpleValue.isPresent()) {
                deserializer().getValueSetter().setField(field(), instance(), simpleValue.get());
                return;
            }

            if (mapValue() instanceof Map) {
                Object childInstance = deserializer().instance(type().getTypeClass(), (Map<String, Object>) mapValue());

                deserializer().getValueSetter().setField(field(), instance(), childInstance);

                tasks().push(new ProcessTaskContainer(childInstance, type(), (Map<String, Object>) mapValue()));
            }
        }
    }
}
