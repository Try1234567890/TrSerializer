package me.tr.trserializer.handlers.java;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.instancers.ProcessInstancer;
import me.tr.trserializer.logger.ProcessLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.util.HashMap;
import java.util.Map;

public class RecordHandler implements TypeHandler {
    private final Process process;

    public RecordHandler(Process process) {
        this.process = process;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        if (!Utility.isAMapWithStringKeys(obj)) {
            getProcess().getLogger().throwable(
                    new TypeMissMatched("The provided object for record deserialization is not a map with canonical constructors key."));
            return null;
        }


        return new ProcessInstancer(getProcess(), (Map<String, Object>) obj).instance(type.getTypeClass());
    }

    @Override
    public Map<String, Object> serialize(Object obj, GenericType<?> type) {
        return getSerializer().serializeAsMap(obj, new HashMap<>());
    }

    public Process getProcess() {
        return process;
    }

    private Serializer getSerializer() {
        return (Serializer) getProcess();
    }
}
