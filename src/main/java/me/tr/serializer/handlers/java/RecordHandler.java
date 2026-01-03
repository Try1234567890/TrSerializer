package me.tr.serializer.handlers.java;

import me.tr.serializer.exceptions.TypeMissMatched;
import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.logger.TrLogger;
import me.tr.serializer.processes.Process;
import me.tr.serializer.instancers.ProcessParamsInstancer;
import me.tr.serializer.processes.deserializer.Deserializer;
import me.tr.serializer.processes.serializer.Serializer;
import me.tr.serializer.types.GenericType;
import me.tr.serializer.utility.Utility;

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
        if (obj instanceof Map<?, ?> map) {
            if (!String.class.isAssignableFrom(Utility.getKeyType(map))) {
                TrLogger.getInstance().exception(
                        new TypeMissMatched("The map keys type is not String.class")
                );
                return null;
            }

            return new ProcessParamsInstancer(getProcess(),
                    (Map<String, Object>) map).instance(type.getTypeClass());
        }
        return obj;
    }

    @Override
    public Map<String, Object> serialize(Object obj, GenericType<?> type) {
        return getSerializer().serializeComplex(obj);
    }

    public Process getProcess() {
        return process;
    }

    private Serializer getSerializer() {
        return (Serializer) getProcess();
    }
}
