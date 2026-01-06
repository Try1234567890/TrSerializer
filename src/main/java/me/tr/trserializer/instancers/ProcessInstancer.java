package me.tr.trserializer.instancers;

import me.tr.trserializer.annotations.Initialize;
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
        Object instance = SINGLETON_INSTANCES.get(clazz);

        if (instance != null)
            return instance;


        Supplier<Object>[] functions = new Supplier[]{
                () -> getConstrByAnnotation(clazz).map(this::instance).orElse(null),
                () -> getMethodByAnnotation(clazz).map(this::instance).orElse(null),
                () -> getEmptyConstr(clazz).map(this::instance).orElse(null),
                () -> instance(getFirstConstr(clazz))
        };

        int i = -1;
        while (instance == null && i++ < 3) {
            instance = functions[i].get();
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
            return new Object[]{getFromProvidedParams(parameter.getName(), parameter.getType())};
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

            Object param = getFromProvidedParams(name, parameter.getType());

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
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.isAnnotationPresent(Initialize.class)) {
                return Optional.of(constructor);
            }
        }
        return Optional.empty();
    }

    private Optional<Constructor<?>> getEmptyConstr(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
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
                            new TypeMissMatched("The return type of the annotated as @Initialize method is not " + clazz.getName().replace("class ", "")));
                    return Optional.empty();
                }

                return Optional.of(method);
            }

        }

        return Optional.empty();
    }

    private Object getFromProvidedParams(String name, Class<?> expected) {
        Object result = null;

        if (getParams().size() == 1) {
            result = getParams().values().iterator().next();

        } else if (getParams().containsKey(name)) {
            result = getParams().get(name);

        } else {
            for (Map.Entry<String, Object> entry : getParams().entrySet()) {
                String key = entry.getKey();
                if (key.equals(name)) {
                    result = entry.getValue();
                }
            }
        }

        if (result == null) {
            return Utility.DEFAULTS.get(expected);
        }

        return getDeserializer().deserialize(result, expected);
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
}