package me.tr.trserializer.processes.process.helper;

import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.cache.ProcessCache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class MethodsExecutor {
    private final Process process;
    private ProcessCache.MethodsCache cache;

    public MethodsExecutor(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    public ProcessCache.MethodsCache getCache() {
        if (cache == null) {
            this.cache = getProcess().getCache().getMethodsCache();
        }
        return cache;
    }

    /**
     * Invokes all registered 'end methods' (cleanup or finalization) for a given instance.
     *
     * @param instance the object instance on which to invoke the methods.
     */
    public void executeEndMethods(Object instance) {
        if (instance == null) return;

        executeMethods(instance, getMethods(instance.getClass(), getProcess().getOptions().getEndMethods()));
    }

    /**
     * Invokes all registered 'start methods' (cleanup or finalization) for a given instance.
     *
     * @param instance the object instance on which to invoke the methods.
     */
    public void executeStartMethods(Object instance) {
        if (instance == null) return;

        executeMethods(instance, getMethods(instance.getClass(), getProcess().getOptions().getStartMethods()));
    }

    /**
     * Invokes a set of methods on the specified instance via reflection.
     *
     * @param instance the target object.
     * @param methods  the methods to be invoked.
     */
    public void executeMethods(Object instance, Method[] methods) {
        if (instance == null) return;


        if (methods == null) return;


        for (Method method : methods) {
            try {
                method.setAccessible(true);
                method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ProcessError("An error occurs while invoking method " + method.getName() + " in class " + instance.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * Resolves method names into {@link Method} objects for a specific class.
     *
     * @param clazz      the class to inspect.
     * @param methodsMap the map of class-to-method-names definitions.
     * @return an array of resolved {@code Method} objects; empty if none found or class not registered.
     */
    private Method[] getMethods(Class<?> clazz, Map<Class<?>, String[]> methodsMap) {
        if (clazz == null) {
            throw new NullPointerException("Class is null");
        }

        if (methodsMap == null || methodsMap.isEmpty() ||
                !methodsMap.containsKey(clazz)) {
            return new Method[0];
        }

        if (getCache().has(clazz)) {
            return getCache().get(clazz);
        }

        String[] methodNames = methodsMap.get(clazz);
        Method[] methods = new Method[methodNames.length];

        for (int i = 0; i < methodNames.length; i++) {
            String name = methodNames[i];
            Method method = getMethod(clazz, name);
            methods[i] = method;
        }

        getCache().put(clazz, methods);
        return methods;
    }

    /**
     * Recursively searches for a method by name in the class hierarchy.
     *
     * @param clazz the starting class for the search.
     * @param name  the name of the method.
     * @return the {@link Method} if found; {@code null} otherwise.
     */
    private Method getMethod(Class<?> clazz, String name) {
        if (clazz == null) {
            throw new NullPointerException("The provided class is null.");
        }

        if (name == null || name.isEmpty()) {
            throw new NullPointerException("The provided method name is null.");
        }

        Class<?> current = clazz;

        while (current != null && !current.equals(Object.class)) {
            try {
                return current.getDeclaredMethod(name);
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }

        throw new ProcessError("No method found in class " + clazz + " with name: " + name);
    }
}
