package me.tr.trserializer.instancer.instanceMethods;

import me.tr.trserializer.annotations.instancer.Initialize;
import me.tr.trserializer.utility.Utility;
import me.tr.trserializer.utility.Wrappers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

public class MethodInstancer implements InstanceMethod {
    private final Method method;


    public MethodInstancer(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T apply(Class<T> cls, Map<String, Object> params) throws InvocationTargetException, IllegalAccessException {
        if (!isStatic(method))
            throw new IllegalArgumentException("The method is not static");

        if (method.getReturnType().equals(Class.class)
                || !Wrappers.isAssignable(method.getReturnType(), cls)) {
            throw new IllegalArgumentException("The provided method do not return an assignable instance of the class " + Utility.getClassName(cls));
        }

        getMethod().setAccessible(true);
        if (isInitializePresent()) {
            AnnotatedMethodInstancer newInstancer = new AnnotatedMethodInstancer(getMethod());
            return newInstancer.apply(cls, params);
        }

        Object[] args = getMethodArgs(cls, params);
        return (T) getMethod().invoke(null, args);
    }

    public Object[] getMethodArgs(Class<?> cls, Map<String, Object> params) {
        boolean hasProvidedParams = params != null && !params.isEmpty();
        if (hasProvidedParams && isNamePresent()) {
            return getParamsByName(params);
        } else if (hasProvidedParams && hasAllParamsDifferentType()) {
            return getParamsByType(params);
        } else {
            return getParamsWithDefaultValues();
        }
    }

    private Object[] getParamsByName(Map<String, Object> params) {
        return ParamsRetriever.getParamsByName(getMethod().getParameters(), params);
    }

    private boolean hasAllParamsDifferentType() {
        return ParamsRetriever.hasAllParamsDifferentType(getMethod().getParameters());
    }

    private Object[] getParamsByType(Map<String, Object> params) {
        return ParamsRetriever.getParamsByType(getMethod().getParameters(), params);
    }

    private Object[] getParamsWithDefaultValues() {
        return ParamsRetriever.getParamsWithDefaultValues(getMethod().getParameters());
    }

    private boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    private boolean isNamePresent() {
        if (getMethod().getParameters().length == 0) return false;
        return getMethod().getParameters()[0].isNamePresent();
    }

    private boolean isInitializePresent() {
        return getMethod().isAnnotationPresent(Initialize.class);
    }
}
