package me.tr.trserializer.instancers;

import me.tr.trserializer.annotations.Initialize;
import me.tr.trserializer.annotations.naming.Naming;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

public class ProcessInstancer implements Instancer {
    private static final Map<Class<?>, Object> SINGLETON_INSTANCES = new HashMap<>();
    private final Process process;
    private final Map<String, Object> params;
    private boolean singleton;
    private boolean failed;
    private Throwable reason;

    public ProcessInstancer(Process process) {
        this(process, new HashMap<>());
    }

    public ProcessInstancer(Process process, Map<String, Object> params) {
        this.process = process;
        this.params = params;
    }

    @Override
    public Object instance(Class<?> clazz) {
        if (clazz == null) {
            TrLogger.exception(new NullPointerException("Class to instance is null"));
            return null;
        }

        List<Supplier<Object>> functions = List.of(
                () -> getProcess().getOptions().getInstance(clazz),
                () -> getMethodByAnnotation(clazz).map(this::instance).orElse(null),
                () -> getConstrByAnnotation(clazz).map(this::instance).orElse(null),
                () -> getEmptyConstr(clazz).map(this::instance).orElse(null),
                () -> instance(getFirstConstr(clazz))
        );

        Object instance = SINGLETON_INSTANCES.get(clazz);
        int i = -1;
        while (instance == null && i++ < 4) {
            // Skip the instance creation with the annotated method.
            if (i == 2 && isNotInstantiable(clazz)) {
                String className = Utility.getClassName(clazz);
                TrLogger.exception(new NoSuchMethodException("The class " + className + " is not instantiable automatically. Add a \"access-modifier static " + className.replace("class ", " ") + " method_name(params...)\" annotated with @Initialize or add a default instance process with \"getProcess().getOptions().addInstance(Class<?>, Supplier)\""));
                return null;
            }

            instance = functions.get(i).get();
        }

        if (instance != null && isSingleton()) {
            SINGLETON_INSTANCES.put(clazz, instance);
        }

        return instance;
    }

    private Object instance(Method method) {
        try {
            method.setAccessible(true);
            Object[] params = resolveParams(method.getParameters(), method.getAnnotation(Initialize.class));

            return method.invoke(null, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            setFailed();
            setReason(e);
            return null;
        }
    }

    private Object instance(Constructor<?> constructor) {
        try {
            constructor.setAccessible(true);
            Object[] params = resolveParams(constructor.getParameters(), constructor.getAnnotation(Initialize.class));

            return constructor.newInstance(params);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            setFailed();
            setReason(e);
            return null;
        }
    }

    private Object[] resolveParams(Parameter[] parameters, Initialize ann) {
        String[] names = ann == null ? null : ann.paramNames();
        this.singleton = ann != null && ann.isSingleton();

        if (names != null && names.length > 0) {
            return resolveParamsByAnnotationNames(parameters, names);
        } else if (ann != null && ann.forceNames()) {
            return resolveParamsByParametersNames(parameters);
        } else if (parameters.length == 1) {
            Parameter parameter = parameters[0];
            return new Object[]{getFromProvidedParams(parameter.getName(), getNamingAnn(parameter), parameter.getType())};
        } else {
            return resolveParamsWithDefaultValues(parameters);
        }

    }

    private Object[] resolveParamsByParametersNames(Parameter[] parameters) {
        return resolveParamsByAnnotationNames(parameters, Arrays.stream(parameters).map(Parameter::getName).toArray(String[]::new));
    }

    private Object[] resolveParamsByAnnotationNames(Parameter[] parameters, String[] paramNames) {
        Object[] params = new Object[paramNames.length];

        if (parameters.length != paramNames.length) {
            TrLogger.exception(new IndexOutOfBoundsException("Mismatch between the number of parameter names provided with the @Initialize annotation and the actual number of constructor parameters."));
            return params;
        }

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String name = paramNames[i];

            Object param = getFromProvidedParams(name, getNamingAnn(parameter), parameter.getType());

            params[i] = param;
        }

        return params;
    }

    private Object[] resolveParamsWithDefaultValues(Parameter[] parameters) {
        Object[] params = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();
            Object param = Utility.DEFAULTS.get(type);

            params[i] = param;
        }

        return params;
    }

    private Optional<Constructor<?>> getConstrByAnnotation(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Initialize.class)) {
                return Optional.of(constructor);
            }
        }
        return Optional.empty();
    }

    private Optional<Constructor<?>> getEmptyConstr(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return Optional.of(constructor);
            }
        }
        return Optional.empty();
    }

    private Constructor<?> getFirstConstr(Class<?> clazz) {
        return clazz.getDeclaredConstructors()[0];
    }

    // public static because EnumHandler use this method too.
    public static Optional<Method> getMethodByAnnotation(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Initialize.class)) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    TrLogger.exception(
                            new IllegalAccessException("The annotated as @Initialize method must be static."));
                    return Optional.empty();
                }

                Class<?> returnType = method.getReturnType();

                if (returnType.isAssignableFrom(Class.class) ||
                        !returnType.equals(clazz)) {
                    TrLogger.exception(
                            new TypeMissMatched("The return type of the annotated as @Initialize method is not " + Utility.getClassName(clazz).replace("class ", "")));
                    return Optional.empty();
                }

                return Optional.of(method);
            }

        }

        return Optional.empty();
    }

    private Object getFromProvidedParams(String name, Naming ann, Class<?> expected) {
        String newName = ann != null ? getProcess().applyNamingStrategy(name, ann) : name;
        Object result = null;

        if (getParams().size() == 1) {
            result = getParams().values().iterator().next();

        } else if (getParams().containsKey(newName)) {
            result = getParams().get(newName);

        } else {
            for (Map.Entry<String, Object> entry : getParams().entrySet()) {
                String key = entry.getKey();
                if (key.equals(newName)) {
                    result = entry.getValue();
                }
            }
        }

        if (result == null) {
            return Utility.DEFAULTS.get(expected);
        }

        return getDeserializer().deserialize(result, expected);
    }

    private Naming getNamingAnn(Parameter parameter) {
        if (parameter.isAnnotationPresent(Naming.class)) {
            return parameter.getAnnotation(Naming.class);
        }

        Executable executable = parameter.getDeclaringExecutable();

        if (executable.isAnnotationPresent(Naming.class)) {
            return executable.getAnnotation(Naming.class);
        }

        Class<?> declaring = executable.getDeclaringClass();

        if (declaring.isAnnotationPresent(Naming.class)) {
            return declaring.getAnnotation(Naming.class);
        }

        return null;
    }

    public Process getProcess() {
        return process;
    }

    private Deserializer getDeserializer() {
        return (Deserializer) getProcess();
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public boolean isSingleton() {
        return singleton;
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    @Override
    public Throwable getReason() {
        return reason;
    }

    private void setFailed() {
        this.failed = true;
    }

    private void setReason(Throwable reason) {
        this.reason = reason;
    }

    @Override
    public void reset() {
        this.failed = false;
        setReason(null);
    }

    /**
     * Checks if the provided class is instantiable.
     *
     * @param clazz The class to check
     * @return {@code true} if it is, otherwise {@code false}.
     */
    private boolean isNotInstantiable(Class<?> clazz) {
        return clazz.isEnum() &&
                clazz.isPrimitive() &&
                clazz.isInterface() &&
                Modifier.isAbstract(clazz.getModifiers());
    }
}