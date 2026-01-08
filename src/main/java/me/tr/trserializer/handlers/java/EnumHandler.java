package me.tr.trserializer.handlers.java;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.logger.Logger;
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
                getProcess().getLogger().debug("The instancer successfully instanced the class " + Utility.getClassName(clazz));
                return instance;
            }

            return Enum.valueOf((Class<Enum>) clazz, name);
        } catch (IllegalArgumentException e) {
            Logger.exception(new RuntimeException("Constant " + name + " not found in Enum " + Utility.getClassName(clazz), e));
            return null;
        }
    }

    @Override
    public String serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Enum<?> en) {
            return en.toString();
        }

        Logger.exception(new TypeMissMatched("The provided class is not an Enum, cannot serialize it."));
        return null;
    }

    public Process getProcess() {
        return process;
    }
}
