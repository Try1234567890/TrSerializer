package me.tr.trserializer.handlers.java;

import me.tr.trserializer.exceptions.DeserializationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.util.Map;

public class EnumHandler implements TypeHandler {
    private final Process process;

    public EnumHandler(Process process) {
        this.process = process;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        Class<?> clazz = type.getTypeClass();

        String name = obj.toString();
        try {
            Object instance = getProcess().getInstancer(Map.of("", name)).instance(clazz);

            if (instance != null) {
                return instance;
            }

            return Enum.valueOf((Class<Enum>) clazz, name);
        } catch (IllegalArgumentException e) {
            throw new DeserializationError("Constant " + name + " not found in Enum " + Utility.getClassName(clazz), e);
        }
    }

    @Override
    public String serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Enum<?> en) {
            return en.toString();
        }

        throw new TypeMissMatched("The provided class is not an Enum, cannot serialize it.");
    }

    public Process getProcess() {
        return process;
    }
}
