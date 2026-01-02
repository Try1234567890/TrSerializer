package me.tr.serializer.processes;

import me.tr.serializer.annotations.Ignore;
import me.tr.serializer.exceptions.InstancerError;
import me.tr.serializer.exceptions.NullsNotAccepted;
import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.instancers.ProcessInstancer;
import me.tr.serializer.registries.HandlersRegistry;
import me.tr.serializer.types.GenericType;

import java.lang.reflect.*;
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

    public <T> T process(Object o, Type clazz) {
        return process(o, new GenericType<>(clazz));
    }

    public <T> T process(Object o, Class<T> clazz) {
        return process(o, new GenericType<>(clazz));
    }

    public abstract <T> T process(Object o, GenericType<T> type);

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

    protected void runEndMethods(Object instance, Map<Class<?>, String[]> methods) {
        runEndMethods(instance, getEndMethods(instance.getClass(), methods));
    }

    protected void runEndMethods(Object instance, Method[] methods) {
        try {
            for (Method method : methods) {
                method.invoke(instance);
            }
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
    }

    protected Method[] getEndMethods(Class<?> clazz, Map<Class<?>, String[]> endMethods) {
        if (endMethods == null ||
                endMethods.isEmpty() ||
                !endMethods.containsKey(clazz))
            return new Method[0];
        String[] methodNames = endMethods.get(clazz);
        Method[] methods = new Method[methodNames.length];
        for (int i = 0; i < methodNames.length; i++) {
            String name = methodNames[i];
            Method method = getMethod(clazz, name);

            if (method == null)
                throw new RuntimeException("No method founds in class " + clazz + " with name: " + name);

            methods[i] = method;
        }
        return methods;
    }

    private Method getMethod(Class<?> clazz, String name) {
        Class<?> current = clazz;

        while (current != null && !current.equals(Object.class)) {
            try {
                return current.getDeclaredMethod(name);
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    protected Object instance(Class<?> clazz) {
        Object instance = getInstancer().instance(clazz);

        if (instance == null)
            throw new InstancerError("An error occurs while instancing " + clazz + ": " + getInstancer().getReason());

        return instance;
    }

    public List<Field> getFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        if (clazz == null)
            return result;

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
        if (o == null)
            return null;

        return getHandler(o.getClass());
    }

    /**
     * Retrieve the handler for the provided class.
     *
     * @param clazz The class to get handler for.
     * @return The handler for the provided class, if class is null return {@code null}.
     */
    protected TypeHandler getHandler(Class<?> clazz) {
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
     */
    protected boolean isPackageBlocked(Object o) {
        if (o == null)
            return true;
        return getOptions().isPackageBlocked(o.getClass().getPackageName());
    }

    /**
     * Checks if the provided object is valid for be processed.
     *
     * @param obj The object to validate.
     * @return {@code true} if it is, otherwise {@code false}.
     * @throws NullsNotAccepted if the object is null and nulls is not accepted.
     */
    protected boolean isValid(Object obj) {
        if (!getOptions().isAcceptNulls() && obj == null)
            throw new NullsNotAccepted("The provided object is null and the process not accept null values.");
        if (obj instanceof Optional<?> opt && (!getOptions().isAcceptEmptyOptional() && opt.isEmpty()))
            throw new NullsNotAccepted("The provided object is an empty optional  and the process not accept empty optional values.");
        return obj != null && !isPackageBlocked(obj);
    }

    /**
     * Checks if the provided field is valid for be processed.
     *
     * @param field The field to validate.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    protected boolean isValid(Field field) {
        int mod = field.getModifiers();
        return !field.isAnnotationPresent(Ignore.class) &&
                (!getOptions().isIgnoreStatic() || !Modifier.isStatic(mod)) &&
                (!getOptions().isIgnoreFinal() || !Modifier.isFinal(mod)) &&
                (!getOptions().isIgnoreTransient() || !Modifier.isTransient(mod));
    }


}