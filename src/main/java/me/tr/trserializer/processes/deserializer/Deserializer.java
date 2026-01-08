package me.tr.trserializer.processes.deserializer;

import me.tr.trserializer.annotations.Aliases;
import me.tr.trserializer.annotations.Essential;
import me.tr.trserializer.annotations.unwrap.Unwrapped;
import me.tr.trserializer.annotations.wrap.Wrapped;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.ProcessTaskContainer;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Three;
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

    protected <T> Optional<T> deserializeAsSimple(Object obj, GenericType<T> type) {
        if (!isValid(obj, type).isSuccess())
            return Optional.empty();

        if (getCache().has(obj)) {
            TrLogger.dbg("Object (" + obj.getClass() + "#" + obj.hashCode() + ") found in cache, reusing it.");
            return Optional.of((T) getCache().get(obj));
        }


        Optional<Map.Entry<PAddon, ?>> addons = processAddons(obj, type, null);


        // Already made returns in processAddon()
        // if one of available addons is valid.
        if (addons.isPresent()) {
            return Optional.of((T) addons.get().getValue());
        }

        return Optional.empty();
    }

    public <T> T deserialize(Object obj, GenericType<T> type) {
        return deserialize(obj, type, null);
    }

    public Object deserializeFromMap(Object instance, Map<String, Object> map) {
        return deserializeFromMap(instance, map, null);
    }

    public void deserialize(Field field, Object instance,
                            Class<?> clazz, Map<String, Object> map) {
        deserialize(field, instance, clazz, map, null);
    }

    protected <T> T deserialize(Object obj, GenericType<T> type,
                                Deque<ProcessTaskContainer> tasks) {
        if (!isValid(obj, type).isSuccess())
            return null;

        Optional<T> simpleResult = deserializeAsSimple(obj, type);

        if (simpleResult.isEmpty()) {
            if (!Utility.isAMapWithStringKeys(obj)) {
                TrLogger.dbg("The provided object is not a map. Cannot set fields values for " + type + ", an empty instance will be used.");
                return makeReturn(obj, instance(type.getTypeClass()), type);
            }

            Map<String, Object> checkedMap = (Map<String, Object>) obj;
            Object instance = instance(type.getTypeClass(), checkedMap);

            cache(obj, instance);

            executeEndMethods(instance);
            return makeReturn(obj, deserializeFromMap(instance, checkedMap, tasks), type);
        }

        return simpleResult.get();
    }


    protected Object deserializeFromMap(Object instance, Map<String, Object> map,
                                        Deque<ProcessTaskContainer> tasks) {
        if (instance == null || map == null) {
            TrLogger.exception(new NullPointerException("Instance or Map is null!"));
            return null;
        }

        Class<?> clazz = instance.getClass();
        Set<Field> fields = getFields(clazz);

        for (Field field : fields) {
            deserialize(field, instance, clazz, map, tasks);
        }

        return instance;
    }


    protected void deserialize(Field field, Object instance,
                               Class<?> clazz, Map<String, Object> values,
                               Deque<ProcessTaskContainer> tasks) {
        field.setAccessible(true);


        try {
            String fieldName = field.getName();
            Object fieldValue = field.get(instance);

            // Skip fields already set by @Initialize entry point.
            if (fieldValue != null) {
                TrLogger.dbg("Field " + fieldName + " in class " + Utility.getClassName(clazz) + " is already set " + fieldValue);
                return;
            }

            if (!isValid(field).isSuccess())
                return;

            GenericType<?> type = new GenericType<>(field);
            Object valueFromMap = getMapValue(field, values);


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
                setField(field, instance, addResult);
                return;
            }

            result(field, valueFromMap, instance, type, values, tasks).ifPresent(DesResult::accept);
        } catch (IllegalAccessException e) {
            TrLogger.exception(new RuntimeException(
                    "An error occurs while setting value for " + field.getName() + " in class " + Utility.getClassName(clazz), e));
        }
    }

    protected Optional<? extends RDesResult> result(Object... obj) {
        if (isParamsOfResultInvalid(obj)) {
            TrLogger.exception(new TypeMissMatched("Params for result building are not valid."));
            return Optional.empty();
        }
        if (!(obj[0] instanceof Field field)) {
            TrLogger.exception(new TypeMissMatched("The param at index 0 is not the processing field."));
            return Optional.empty();
        }
        if (!(obj[3] instanceof GenericType<?> type)) {
            TrLogger.exception(new TypeMissMatched("The param at index 3 is not the value type."));
            return Optional.empty();
        }
        Object uncheckedResultMap = obj[4];
        if (!Utility.isAMapWithStringKeys(uncheckedResultMap, true)) {
            TrLogger.exception(new TypeMissMatched("The param at index 3 is not the map result."));
            return Optional.empty();
        }
        return Optional.of(new RDesResult(this, field, obj[1], obj[2], type, (Map<String, Object>) uncheckedResultMap));
    }

    protected void setField(Field field, Object instance, Object value) {
        String fieldName = field.getName();
        String className = Utility.getClassName(instance.getClass());
        try {

            if (field.isAnnotationPresent(Essential.class)
                    && !isValid(value).isSuccess()) {
                TrLogger.exception(new NullPointerException("The value for field " + fieldName + " in class " + className + " hasn't pass the validation and the field is annotated with @Essential."));
                return;
            }

            field.set(instance, value);
        } catch (IllegalAccessException e) {
            TrLogger.exception(new RuntimeException("An error occurs while setting the value to " + fieldName + " in class " + className, e));
        }
    }

    protected Object getMapValue(Field field, Map<String, Object> map) {
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

    protected Set<String> getAliases(Field field) {
        Set<String> result = new HashSet<>();
        Class<?> declaringClass = field.getDeclaringClass();
        String fieldName = applyNamingStrategy(field);

        for (Three<Class<?>, String, String[]> aliases : getOptions().getAliases()) {
            if (aliases.key().equals(declaringClass) &&
                    compare(fieldName, aliases.value())) {
                result.addAll(List.of(aliases.subValue()));
                break;
            }
        }

        if (field.isAnnotationPresent(Aliases.class)) {
            Aliases ann = field.getAnnotation(Aliases.class);
            result.addAll(List.of(ann.aliases()));
        }

        return result;
    }

    protected boolean compare(String s, String s2) {
        return getOptions().isIgnoreCase() ? s.equalsIgnoreCase(s2) : s.equals(s2);
    }

    @Override
    protected Map<Class<?>, String[]> getEndMethods() {
        return getOptions().getEndMethods();
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

            deserializer().setField(field(), instance(), deserialized);
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
                deserializer().setField(field(), instance(), simpleValue.get());
                return;
            }

            if (mapValue() instanceof Map) {
                Object childInstance = deserializer().instance(type().getTypeClass(), (Map<String, Object>) mapValue());

                deserializer().setField(field(), instance(), childInstance);

                tasks().push(new ProcessTaskContainer(childInstance, type(), (Map<String, Object>) mapValue()));
            }
        }
    }
}
