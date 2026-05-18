package me.tr.trserializer.instancer.instanceMethods;

import me.tr.trserializer.annotations.filter.Essential;
import me.tr.trserializer.utility.DefaultValues;
import me.tr.trserializer.utility.Wrappers;

import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParamsRetriever {


    static Object[] getParamsWithDefaultValues(Parameter[] parameters) {
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();
            Object value = DefaultValues.getDefaultValue(type);

            args[i] = value;
        }

        return args;
    }

    static Object[] getParamsByType(Parameter[] parameters, Map<String, Object> params) {
        Collection<Object> values = params.values();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();
            Object value = getValueWithType(type, values);

            args[i] = value;
        }

        return args;
    }

    private static Object getValueWithType(Class<?> cls, Collection<Object> params) {
        Object value = null;

        for (Object param : params) {
            if (param == null) continue;
            Class<?> paramCls = param.getClass();
            if (paramCls.equals(cls) ||
                    Wrappers.getWrapper(paramCls).equals(Wrappers.getWrapper(cls))) {
                value = param;
            }
        }

        return value;
    }

    static Object[] getParamsByName(Parameter[] parameters, Map<String, Object> params) {
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String paramName = parameter.getName();
            Object value = params.get(paramName);

            if (value == null && parameter.isAnnotationPresent(Essential.class))
                throw new IllegalArgumentException("The parameter " + paramName + " is not annotated with @Essential and is null.");

            args[i] = value;
        }

        return args;
    }

    static boolean hasAllParamsDifferentType(Parameter[] parameters) {
        Set<Class<?>> types = new HashSet<>();

        for (Parameter parameter : parameters) {
            Class<?> type = parameter.getType();

            if (types.contains(type))
                return false;

            types.add(type);
        }

        return true;
    }
}
