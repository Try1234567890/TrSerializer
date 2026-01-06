package me.tr.trserializer.processes.process;

import me.tr.trserializer.annotations.Ignore;
import me.tr.trserializer.converters.Converter;
import me.tr.trserializer.exceptions.InstancerError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.instancers.ProcessInstancer;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.addons.ProcessAddon;
import me.tr.trserializer.registries.ConvertersRegistry;
import me.tr.trserializer.registries.HandlersRegistry;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * This class is the base for all processes.
 *
 * <p>
 * Is composed of:
 * <li>ProcessOptions -> All the options to modify the comportment in some cases.</li>
 * <li>ProcessInstancer -> The instancer used from handlers to instance provided classes.</li>
 * <li>ProcessCache -> The cache used from handlers to preserve object identity or block StackOverFlow exceptions.</li>
 */
public abstract class Process {
    protected static final String CACHE_ID = "TrSerializer:!__#@ID__!";
    protected static final String CACHE_REF = "TrSerializer:!__#@REF__!";
    private final IdentityHashMap<TypeHandler, Map.Entry<Object, GenericType<?>>>
            RUNNING_HANDLERS = new IdentityHashMap<>();
    private ProcessContext context;


    public ProcessContext getContext() {
        return context;
    }

    protected void setContext(ProcessContext context) {
        this.context = context;
    }

    public ProcessInstancer getInstancer(Map<String, Object> params) {
        return getContext().getInstancer(params);
    }

    public ProcessOptions getOptions() {
        return getContext().getOptions();
    }

    public ProcessCache getCache() {
        return getContext().getCache();
    }

    public IdentityHashMap<TypeHandler,
            Map.Entry<Object, GenericType<?>>> getRunningHandlers() {
        return RUNNING_HANDLERS;
    }

    /*
     * ===============================
     *      UTILITY FOR HANDLERS
     * ===============================
     */

    protected <T> Optional<T> processAddons(Object obj, GenericType<T> type) {
        for (ProcessAddon addon : getContext().getAddons()) {
            String addonName = addon.getName();
            try {
                TrLogger.dbg("Executing " + addonName);
                Optional<Object> resultOpt = addon.process(this, obj, type);
                if (resultOpt.isPresent()) {
                    Object result = resultOpt.get();

                    getCache().put(obj, result);

                    TrLogger.dbg("The addon " + addonName + " returns a valid result. Returning: " + result);

                    return Optional.ofNullable(makeReturn(result, obj, type));
                }
            } catch (Exception e) {
                TrLogger.exception(new RuntimeException("The addon " + addon.getName() + " thrown an exception.", e));

            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    protected <T> T makeReturn(Object object, Object instance, GenericType<T> type) {
        if (object == null)
            return null;

        /*
         * Get wrapper returns the wrapper of primitive
         * if provided class is primitive, otherwise
         * returns the provided class.
         */
        Class<?> expected = Utility.getWrapper(type.getTypeClass());
        Class<?> objClass = Utility.getWrapper(object.getClass());

        T result = null;


        if (Object.class.equals(expected)
                || expected.isAssignableFrom(objClass)) {
            result = (T) object;
        }

        if (!(object instanceof List<?>)
                && List.class.isAssignableFrom(expected)) {
            result = (T) new ArrayList<>(List.of(object));
        }

        if (!(object instanceof String)
                && String.class.isAssignableFrom(expected)) {
            result = (T) String.valueOf(object);
        }

        if (object instanceof Number num
                && Number.class.isAssignableFrom(expected)) {
            Converter<Number, ?> converter = ConvertersRegistry.getConverter(Number.class, expected);
            if (converter != null) {
                result = (T) converter.primitive(num);
            }
        }

        if (result == null) {
            TrLogger.exception(new TypeMissMatched(
                    "The result of " + this.getClass().getSimpleName() + " process (" + objClass + ") cannot be converted to " + expected));
        }

        runEndMethods(instance);

        return result;
    }


    protected void runEndMethods(Object instance) {
        if (instance == null) {
            TrLogger.exception(
                    new NullPointerException("Instance is null."));
            return;
        }
        runEndMethods(instance, getEndMethods(instance.getClass(), getMethods()));
    }

    protected abstract Map<Class<?>, String[]> getMethods();

    private void runEndMethods(Object instance, Method[] methods) {
        if (instance == null) {
            TrLogger.exception(
                    new NullPointerException("Instance is null."));
            return;
        }

        if (methods == null) {
            TrLogger.exception(
                    new NullPointerException("Methods is null."));
            return;
        }

        for (Method method : methods) {
            try {
                method.setAccessible(true);
                method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                TrLogger.exception(new RuntimeException("An error occurs while invoking method " + method.getName() + " in class " + instance.getClass().getSimpleName(), e));
            }
        }
    }

    /**
     * Retrieve all ends methods for the provided class
     *
     * @param clazz      The class to get methods from and for.
     * @param endMethods All ends methods of this process.
     * @return An array of all methods.<p> (if class hasn't ends methods, an empty array will be returned)
     */
    private Method[] getEndMethods(Class<?> clazz, Map<Class<?>, String[]> endMethods) {
        if (clazz == null) {
            TrLogger.exception(new NullPointerException("Class is null"));
            return new Method[0];
        }

        if (endMethods == null || endMethods.isEmpty() ||
                !endMethods.containsKey(clazz)) {
            return new Method[0];
        }

        String[] methodNames = endMethods.get(clazz);
        Method[] methods = new Method[methodNames.length];

        for (int i = 0; i < methodNames.length; i++) {
            String name = methodNames[i];
            Method method = getMethod(clazz, name);

            if (method == null) {
                TrLogger.exception(new NoSuchMethodException("No method founds in class " + clazz + " with name: " + name));
                continue;
            }

            methods[i] = method;
        }

        return methods;
    }

    /**
     * Search the method with the provided name
     * in the provided class, or it super classes.
     *
     * @param clazz The class to search in.
     * @param name  The method name to search.
     * @return The method if found, otherwise {@code null}.
     */
    private Method getMethod(Class<?> clazz, String name) {
        if (clazz == null) {
            TrLogger.exception(new NullPointerException("The provided class is null."));
            return null;
        }

        if (name == null || name.isEmpty()) {
            TrLogger.exception(new NullPointerException("The provided method name is null."));
            return null;
        }

        Class<?> current = clazz;

        while (current != null && !current.equals(Object.class)) {
            try {
                return current.getDeclaredMethod(name);
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }

        TrLogger.exception(new NoSuchMethodException("No method found in class " + clazz + " with name: " + name));
        return null;
    }

    /**
     * Instance the provided class with
     * process options help.
     *
     * @param clazz The class to instance.
     * @param map The params to provide to initialize method.
     * @return A new instance of the class.
     * @throws InstancerError if the instancer fail.
     */
    public Object instance(Class<?> clazz, Map<String, Object> map) {
        ProcessInstancer instancer = getInstancer(map);

        Object instance = instancer.instance(clazz);

        if (instance == null) {
            TrLogger.exception(new InstancerError("An error occurs while instancing " + clazz + ": " + instancer.getReason()));
            instancer.reset();
            return null;
        }

        return instance;
    }

    /**
     * Instance the provided class with
     * process options help.
     *
     * @param clazz The class to instance.
     * @return A new instance of the class.
     * @throws InstancerError if the instancer fail.
     */
    public Object instance(Class<?> clazz) {
        ProcessInstancer instancer = getInstancer(new HashMap<>());
        Object instance = instancer.instance(clazz);

        if (instance == null) {
            TrLogger.exception(new InstancerError("An error occurs while instancing " + clazz + ": " + instancer.getReason()));
            instancer.reset();
            return null;
        }

        return instance;
    }

    /**
     * Retrieve the fields from the provided class
     * and all super classes until reaches {@link Object},
     * according to each field validation
     * (see: {@link #isValid(Field)})
     *
     * @param clazz The class to get fields from.
     * @return a set with all retrieved fields.
     */
    public Set<Field> getFields(Class<?> clazz) {
        Set<Field> result = new HashSet<>();

        if (clazz == null) {
            TrLogger.exception(
                    new NullPointerException("The provide class is null."));
            return result;
        }

        Class<?> current = clazz;
        while (current != null && !current.equals(Object.class)) {
            Arrays.stream(current.getDeclaredFields())
                    .filter(this::isValid)
                    .forEach(result::add);

            current = current.getSuperclass();
        }

        return result;
    }

    /**
     * Retrieve the handler for the provided class.
     *
     * @param clazz The class to get handler for.
     * @return The handler for the provided class, if class is null return {@code null}.
     */
    public Optional<TypeHandler> getHandler(Class<?> clazz) {
        // No null-checks needed, simply if clazz is null returns Optional.empty().
        return HandlersRegistry.getInstance().get(clazz, this);
    }

    /*
     * ===============================
     * VALIDATION CHECKS FOR HANDLERS
     * ===============================
     */

    /**
     * Checks if the provided object and type is valid for be processed.
     *
     * @param obj  The object to validate.
     * @param type The type to validate.
     * @return {@code true} if it is, otherwise {@code false}.
     * @throws NullPointerException if the object is null and nulls is not accepted.
     */
    protected boolean isValid(Object obj, GenericType<?> type) {
        if (type == null) {
            TrLogger.exception(new NullPointerException("Object or type is null!"));
            return false;
        }

        return isValid(obj);
    }

    /**
     * Checks if the provided object is valid for be processed.
     *
     * @param obj The object to validate.
     * @return {@code true} if it is, otherwise {@code false}.
     * @throws NullPointerException if the object is null and nulls is not accepted.
     */
    protected boolean isValid(Object obj) {
        if (obj == null) {
            if (!getOptions().isAcceptNulls())
                TrLogger.exception(new NullPointerException("The provided object is null and the process not accept null values."));

            return false;
        }

        if (obj instanceof Optional<?> opt && opt.isEmpty()) {
            if (!getOptions().isAcceptEmptyOptional()) {
                TrLogger.exception(new NullPointerException("The provided object is an empty optional and the process not accept empty optional values."));
                return false;
            }
        }

        return !isPackageBlocked(obj);
    }

    /**
     * Checks if object package is blocked.
     *
     * @param o The object to verify.
     * @return {@code true} if is blocked, otherwise false.
     * @throws NullPointerException if the object is null.
     */
    private boolean isPackageBlocked(Object o) {
        if (o == null) {
            TrLogger.exception(new NullPointerException("The object is null."));
            return true;
        }

        return getOptions().isPackageBlocked(o.getClass().getPackageName());
    }

    /**
     * Checks if the provided field is valid for be processed.
     * <p>
     * Field is NOT valid if:
     * <ul>
     *     <li>Has {@link Ignore} annotation</li>
     *     <li>Is static and {@link ProcessOptions#isIgnoreStatic()} returns {@code true}</li>
     *     <li>Is final and {@link ProcessOptions#isIgnoreFinal()} returns {@code true}</li>
     *     <li>Is transient and {@link ProcessOptions#isIgnoreTransient()} returns {@code true}</li>
     * </ul>
     *
     * @param field The field to validate.
     * @return {@code true} if it is, otherwise {@code false}.
     * @throws NullPointerException if the field is null
     */
    protected boolean isValid(Field field) {
        if (field == null) {
            TrLogger
                    .exception(new NullPointerException("The field to validate is null."));
            return false;
        }

        int mod = field.getModifiers();
        return !field.isAnnotationPresent(Ignore.class) &&
                (!getOptions().isIgnoreStatic() || !Modifier.isStatic(mod)) &&
                (!getOptions().isIgnoreFinal() || !Modifier.isFinal(mod)) &&
                (!getOptions().isIgnoreTransient() || !Modifier.isTransient(mod));
    }


}