package me.tr.trserializer.instancer.instanceMethods;

import me.tr.trserializer.annotations.instancer.Initialize;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ConstructorInstancer implements InstanceMethod {
    private final Constructor<?> constructor;


    public ConstructorInstancer(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T apply(Class<T> cls, Map<String, Object> params) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!constructor.getDeclaringClass().equals(cls))
            throw new IllegalArgumentException("The provided constructor is not of the class " + Utility.getClassName(cls));


        getConstructor().setAccessible(true);

        if (isInitializePresent()) {
            AnnotatedConstructorInstancer newInstancer = new AnnotatedConstructorInstancer(getConstructor());
            return newInstancer.apply(cls, params);
        }

        Object[] args = getConstructorArgs(cls, params);
        return (T) getConstructor().newInstance(args);
    }

    public Object[] getConstructorArgs(Class<?> cls, Map<String, Object> params) {
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
        return ParamsRetriever.getParamsByName(getConstructor().getParameters(), params);
    }

    private boolean hasAllParamsDifferentType() {
        return ParamsRetriever.hasAllParamsDifferentType(getConstructor().getParameters());
    }

    private Object[] getParamsByType(Map<String, Object> params) {
        return ParamsRetriever.getParamsByType(getConstructor().getParameters(), params);
    }

    private Object[] getParamsWithDefaultValues() {
        return ParamsRetriever.getParamsWithDefaultValues(getConstructor().getParameters());
    }

    private boolean isNamePresent() {
        if (getConstructor().getParameters().length == 0) return false;
        return getConstructor().getParameters()[0].isNamePresent();
    }

    private boolean isInitializePresent() {
        return getConstructor().isAnnotationPresent(Initialize.class);
    }
}











