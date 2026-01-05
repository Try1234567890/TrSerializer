package me.tr.trserializer.instancers;

import me.tr.trserializer.processes.Process;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Map;

public class ProcessParamsInstancer extends ParamsInstancer {
    private final Process process;

    public ProcessParamsInstancer(Process process, Map<String, Object> params) {
        super(params);
        this.process = process;
    }

    protected Object[] resolveArgsByAnnotation(Constructor<?> constructor, String[] paramNames) {
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[paramNames.length];

        if (parameters.length == 1 &&
                getParams().size() == 1 &&
                paramNames.length == 0)
            return new Object[]{getParams().values().toArray()[0]};

        for (int i = 0; i < paramNames.length; i++) {
            String name = paramNames[i];
            Class<?> type = parameters[i].getType();
            Object value = getDeserializer().deserialize(getParams().get(name), type);

            if (value == null)
                value = Utility.DEFAULTS.get(type);

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
            Class<?> type = param.getType();
            String paramName = param.getName();

            Object value = getDeserializer().deserialize(getParams().get(paramName), type);

            if (value == null)
                value = Utility.DEFAULTS.get(type);

            args[i] = value;
        }

        return args;
    }

    public Process getProcess() {
        return process;
    }


    private Deserializer getDeserializer() {
        return (Deserializer) process;
    }

    private Serializer getSerializer() {
        return (Serializer) process;
    }
}