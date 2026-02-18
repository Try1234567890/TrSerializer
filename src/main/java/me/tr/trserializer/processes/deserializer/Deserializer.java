package me.tr.trserializer.processes.deserializer;

import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.processes.deserializer.helper.DAddonsManager;
import me.tr.trserializer.processes.deserializer.helper.DValueRetriever;
import me.tr.trserializer.processes.deserializer.helper.DValueSetter;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.ProcessTaskContainer;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public class Deserializer extends Process {

    public Deserializer() {
        setContext(new DeserializerContext(this));
    }

    /**
     * @return The cache for this deserializer.
     */
    @Override
    public DeserializerCache getCache() {
        return (DeserializerCache) super.getCache();
    }

    /**
     * @return The context for this deserializer.
     */
    @Override
    public DeserializerContext getContext() {
        return (DeserializerContext) super.getContext();
    }

    /**
     * @return The options for this deserializer.
     */
    @Override
    public DeserializerOptions getOptions() {
        return (DeserializerOptions) super.getOptions();
    }

    /**
     * @return The value retriever for this deserializer.
     */
    public DValueRetriever getValueRetriever() {
        return getContext().getValueRetriever();
    }

    /**
     * @return The value setter for this deserializer.
     */
    public DValueSetter getValueSetter() {
        return getContext().getValueSetter();
    }

    /**
     * @return The addons manager for this deserializer.
     */
    public DAddonsManager getAddonsManager() {
        return getContext().getAddonsManager();
    }


    public <T> T deserialize(Object obj, T type) {
        if (type == null)
            throw new NullPointerException("Object or type is null!");

        return (T) deserialize(obj, type.getClass());
    }

    public <T> T deserialize(Object obj, Class<T> type) {
        if (type == null)
            throw new NullPointerException("Object or type is null!");

        return deserialize(obj, new GenericType<>(type));
    }

    protected <T> Optional<T> deserializeAsSimple(Object obj, GenericType<T> type) {
        if (!getProcessValidator().isValid(obj, type).isSuccess())
            return Optional.empty();

        getMethodsExecutor().executeStartMethods(obj);

        if (getCache().has(obj))
            return Optional.of((T) getCache().get(obj));

        Optional<Object> addons = getAddonsManager().getValidAddon(obj, type);


        // Already made returns in processAddon()
        // if one of available addons is valid.
        if (addons.isPresent()) {
            return (Optional<T>) addons;
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
            if (!Utility.isAMapWithStringKeys(obj))
                return validate(obj, instance(type.getTypeClass()), type);


            Map<String, Object> checkedMap = (Map<String, Object>) obj;
            Object instance = instance(type.getTypeClass(), checkedMap);

            cache(obj, instance);


            return validate(obj, deserializeFromMap(instance, checkedMap, tasks), type);
        }

        return simpleResult.get();
    }


    protected Object deserializeFromMap(Object obj, Map<String, Object> map,
                                        Deque<ProcessTaskContainer> tasks) {
        if (obj == null || map == null)
            throw new NullPointerException("Object or Map is null!");

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
            Object fieldValue = field.get(obj);

            // Skip fields already set by @Initialize entry point.
            if (fieldValue != null)
                return;


            if (!getProcessValidator().isValid(field).isSuccess())
                return;

            GenericType<?> type = new GenericType<>(field);
            Object valueFromMap = getValueRetriever().getMapValue(field, values);


            Optional<Object> addons = getAddonsManager().getValidAddon(valueFromMap, values, type, field);

            if (addons.isPresent()) {
                Object addResult = addons.get();
                getValueSetter().setField(field, obj, addResult);
                return;
            }

            result(field, valueFromMap, obj, type, values, tasks).ifPresent(DesResult::accept);
        } catch (IllegalAccessException e) {
            throw new ProcessError("An error occurs while setting value for " + field.getName() + " in class " + Utility.getClassName(clazz), e);
        }
    }

    protected Optional<? extends RDesResult> result(Object... obj) {
        if (isParamsOfResultInvalid(obj)) {
            throw new TypeMissMatched("Params for result building are not valid.");
        }
        if (!(obj[0] instanceof Field field)) {
            throw new TypeMissMatched("The param at index 0 is not the processing field.");
        }
        if (!(obj[3] instanceof GenericType<?> type)) {
            throw new TypeMissMatched("The param at index 3 is not the value type.");
        }
        Object uncheckedResultMap = obj[4];
        if (!Utility.isAMapWithStringKeys(uncheckedResultMap, true)) {
            throw new TypeMissMatched("The param at index 3 is not the map result.");
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
