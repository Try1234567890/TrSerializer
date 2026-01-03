package me.tr.serializer.processes;

import me.tr.serializer.annotations.Ignore;
import me.tr.serializer.converters.Converter;
import me.tr.serializer.exceptions.InstancerError;
import me.tr.serializer.exceptions.TypeMissMatched;
import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.instancers.ProcessInstancer;
import me.tr.serializer.logger.TrLogger;
import me.tr.serializer.registries.ConvertersRegistry;
import me.tr.serializer.registries.HandlersRegistry;
import me.tr.serializer.types.GenericType;
import me.tr.serializer.utility.Utility;

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
    private final ProcessOptions options = new ProcessOptions(this);
    private final ProcessInstancer instancer = new ProcessInstancer(this);

    /*
     * ===============================
     *      INSTANCER & OPTIONS
     * ===============================
     */

    /**
     * Retrieve the options of this process.
     *
     * @return the {@link ProcessOptions} of this instance.
     */
    public ProcessOptions getOptions() {
        return options;
    }

    /**
     * Retrieve the instancer if this process.
     *
     * @return the {@link ProcessInstancer} if this instance.
     */
    public ProcessInstancer getInstancer() {
        return instancer;
    }

    /*
     * ===============================
     *      UTILITY FOR HANDLERS
     * ===============================
     */

    @SuppressWarnings("unchecked")
    protected <T> T checkReturn(Object object, GenericType<T> type) {
        if (object == null)
            return null;

        /*
         * Get wrapper returns the wrapper of primitive
         * if provided class is primitive, otherwise
         * returns the provided class.
         */
        Class<?> expected = Utility.getWrapper(type.getTypeClass());
        Class<?> objClass = Utility.getWrapper(object.getClass());

        if (!(object instanceof List<?>)
                && List.class.isAssignableFrom(expected)) {
            return (T) new ArrayList<>(List.of(object));
        }

        if (!(object instanceof String)
                && String.class.isAssignableFrom(expected)) {
            return (T) String.valueOf(object);
        }

        if (object instanceof Number num
                && Number.class.isAssignableFrom(expected)) {
            Converter<Number, ?> converter = ConvertersRegistry.getConverter(Number.class, expected);
            if (converter != null) {
                object = converter.primitive(num);
            }
        }


        if (Object.class.equals(expected)
                || expected.isAssignableFrom(objClass)) {
            return (T) object;
        }

        TrLogger.getInstance().exception(
                new TypeMissMatched("The result of serialization process (" + objClass + ") cannot be converted to the requested type: " + expected)
        );

        return null;
    }

    protected void runEndMethods(Object instance, Map<Class<?>, String[]> methods) {
        if (instance == null) {
            TrLogger.getInstance().exception(
                    new NullPointerException("Instance is null."));
            return;
        }
        runEndMethods(instance, getEndMethods(instance.getClass(), methods));
    }

    private void runEndMethods(Object instance, Method[] methods) {
        if (instance == null) {
            TrLogger.getInstance().exception(
                    new NullPointerException("Instance is null."));
            return;
        }

        if (methods == null) {
            TrLogger.getInstance().exception(
                    new NullPointerException("Methods is null."));
            return;
        }

        for (Method method : methods) {
            try {
                method.setAccessible(true);
                method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                TrLogger.getInstance().exception(new RuntimeException("An error occurs while invoking method " + method.getName() + " in class " + instance.getClass().getSimpleName(), e));
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
            TrLogger.getInstance().exception(new NullPointerException("Class is null"));
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
                TrLogger.getInstance().exception(new NoSuchMethodException("No method founds in class " + clazz + " with name: " + name));
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
            TrLogger.getInstance().exception(new NullPointerException("The provided class is null."));
            return null;
        }

        if (name == null || name.isEmpty()) {
            TrLogger.getInstance().exception(new NullPointerException("The provided method name is null."));
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

        TrLogger.getInstance().exception(new NoSuchMethodException("No method found in class " + clazz + " with name: " + name));
        return null;
    }

    /**
     * Instance the provided class with
     * process options help.
     *
     * @param clazz The class to instance.
     * @return A new instance of the class.
     * @throws InstancerError if the instancer fail.
     */
    protected Object instance(Class<?> clazz) {
        Object instance = getInstancer().instance(clazz);

        if (instance == null) {
            TrLogger.getInstance().exception(new InstancerError("An error occurs while instancing " + clazz + ": " + getInstancer().getReason()));
            getInstancer().reset();
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
     * @return a list with all retrieved fields.
     */
    public List<Field> getFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();

        if (clazz == null) {
            TrLogger.getInstance().exception(
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
     * Retrieve the handler fot the provide object.
     *
     * @param o The object to get handler for.
     * @return The handler for the provided object, if object is null return {@code null}.
     */
    protected TypeHandler getHandler(Object o) {
        if (o == null) {
            TrLogger.getInstance().exception(new NullPointerException("The object is null."));
            return null;
        }

        return getHandler(o.getClass());
    }

    /**
     * Retrieve the handler for the provided class.
     *
     * @param clazz The class to get handler for.
     * @return The handler for the provided class, if class is null return {@code null}.
     */
    protected TypeHandler getHandler(Class<?> clazz) {
        // No null-checks needed, simply if clazz is null returns null.
        return HandlersRegistry.getInstance().get(clazz, this);
    }

    /*
     * ===============================
     * VALIDATION CHECKS FOR HANDLERS
     * ===============================
     */

    /**
     * Checks if object package is blocked.
     *
     * @param o The object to verify.
     * @return {@code true} if is blocked, otherwise false.
     * @throws NullPointerException if the object is null.
     */
    protected boolean isPackageBlocked(Object o) {
        if (o == null) {
            TrLogger.getInstance()
                    .exception(new NullPointerException("The object is null."));
            return true;
        }

        return getOptions().isPackageBlocked(o.getClass().getPackageName());
    }

    /**
     * Checks if the provided object is valid for be processed.
     *
     * @param obj The object to validate.
     * @return {@code true} if it is, otherwise {@code false}.
     * @throws NullPointerException if the object is null and nulls is not accepted.
     */
    protected boolean isValid(Object obj) {
        if (!getOptions().isAcceptNulls() && obj == null) {
            TrLogger.getInstance().error(new NullPointerException("The provided object is null and the process not accept null values."));
            return false;
        }

        if (obj instanceof Optional<?> opt && (!getOptions().isAcceptEmptyOptional() && opt.isEmpty())) {
            TrLogger.getInstance().error(new NullPointerException("The provided object is an empty optional and the process not accept empty optional values."));
            return false;
        }

        return obj != null && !isPackageBlocked(obj);
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
            TrLogger.getInstance()
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