package me.tr.serializer.instancers;

import me.tr.serializer.annotations.Initialize;
import me.tr.serializer.processes.deserializer.Deserializer;
import me.tr.serializer.utility.Utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ParamsInstancer implements Instancer {
    private final Map<Class<?>, Object> SINGLETON_INSTANCES = new HashMap<>();
    private Map<String, Class<?>> paramsType;
    private Map<String, Object> params;
    private boolean singleton;
    private boolean failed;
    private Throwable reason;


    public ParamsInstancer(Map<String, Object> params) {
        this.params = params;
        this.paramsType = new HashMap<>();
        params.forEach((key, value) -> paramsType.put(key, value.getClass()));
    }

    @Override
    public Object instance(Class<?> clazz) {
        if (isSingleton()
                && SINGLETON_INSTANCES.containsKey(clazz)) {
            return SINGLETON_INSTANCES.get(clazz);
        }

        try {
            Constructor<?> selectedConstructor = null;
            Object[] args = null;

            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                if (constructor.isAnnotationPresent(Initialize.class)) {
                    /*
                     * Map params name with their type to avoid
                     * repeat this operation for each param later.
                     */
                    Arrays.stream(constructor.getParameters()).forEach(param -> paramsType.put(param.getName(), param.getType()));

                    Initialize annotation = constructor.getAnnotation(Initialize.class);
                    args = resolveArgsByAnnotation(constructor, annotation.paramNames());
                    selectedConstructor = constructor;
                    break;
                }
            }

            if (selectedConstructor == null) {
                Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                args = resolveArgsByNameAndType(constructor);
                selectedConstructor = constructor;
            }

            selectedConstructor.setAccessible(true);
            Object createdInstance = selectedConstructor.newInstance(args);

            if (isSingleton())
                SINGLETON_INSTANCES.put(clazz, createdInstance);

            return createdInstance;

        } catch (Exception e) {
            fail(e);
        }
        return null;
    }

    protected Object[] resolveArgsByAnnotation(Constructor<?> constructor, String[] paramNames) {
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];


        if (parameters.length == 1 && getParams().size() == 1) {
            args[0] = getParams().values().toArray()[0];
            return args;
        }

        if (paramNames == null
                || paramNames.length == 0) {
            return Arrays.stream(parameters).map(parameter -> Utility.DEFAULTS.get(parameter.getType())).toArray();
        }

        for (int i = 0; i < paramNames.length; i++) {
            String name = paramNames[i];

            Object value = params.get(name);

            if (value == null)
                value = Utility.DEFAULTS.get(parameters[i].getType());

            args[i] = value;
        }

        return args;
    }

    protected Object[] resolveArgsByNameAndType(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];

        if (parameters.length == 1 && getParams().size() == 1) {
            args[0] = getParams().values().toArray()[0];
            return args;
        }

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String paramName = param.getName();

            Object value = params.get(paramName);

            if (value == null)
                value = Utility.DEFAULTS.get(param.getType());

            args[i] = value;
        }

        return args;
    }

    private void fail(Throwable reason) {
        setFailed();
        setReason(reason);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    protected Map<String, Class<?>> getParamsType() {
        return paramsType;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params != null ? params : new HashMap<>();
        this.paramsType.clear();
        this.params.forEach((key, value) -> paramsType.put(key, value.getClass()));
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
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