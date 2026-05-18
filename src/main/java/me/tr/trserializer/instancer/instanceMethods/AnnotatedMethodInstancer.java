package me.tr.trserializer.instancer.instanceMethods;

import me.tr.trserializer.annotations.instancer.Initialize;
import me.tr.trserializer.instancer.SingletonInstances;
import me.tr.trserializer.utility.SLogger;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class AnnotatedMethodInstancer extends MethodInstancer {
    private final Initialize initialize;

    public AnnotatedMethodInstancer(Method method, Initialize initialize) {
        super(method);
        this.initialize = initialize;
    }

    public AnnotatedMethodInstancer(Method method) {
        super(method);
        if (!method.isAnnotationPresent(Initialize.class))
            throw new IllegalArgumentException("The provided method has not the @Initialize annotation ");

        this.initialize = method.getAnnotation(Initialize.class);
    }

    public Initialize getInitialize() {
        return initialize;
    }

    @Override
    public <T> T apply(Class<T> cls, Map<String, Object> params) throws InvocationTargetException, IllegalAccessException {
        T instance = super.apply(cls, params);

        if (isSingleton() && !SingletonInstances.isSingleton(cls)) {
            SLogger.LOGGER.info("New singleton registered: " + Utility.getClassName(cls));
            SingletonInstances.newSingleton(cls, instance);
        }

        return instance;
    }

    @Override
    public Object[] getMethodArgs(Class<?> cls, Map<String, Object> params) {
        if (isNameSpecified()) {
            String[] names = getInitialize().paramNames();
            return getParamsWithNames(names, params);
        }

        return super.getMethodArgs(cls, params);
    }

    private Object[] getParamsWithNames(String[] names, Map<String, Object> params) {
        Object[] args = new Object[names.length];

        for (int i = 0; i < names.length; i++) {
            // TODO: Find a way to check if
            //       the parameter is annotated with @Essential.
            args[i] = params.get(names[i]);
        }

        return args;
    }

    private boolean isNameSpecified() {
        return getInitialize().paramNames() != null;
    }

    private boolean isSingleton() {
        return getInitialize().isSingleton();
    }
}
