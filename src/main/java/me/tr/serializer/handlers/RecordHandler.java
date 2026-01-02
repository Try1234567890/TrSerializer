package me.tr.serializer.handlers;

import me.tr.serializer.processes.Process;
import me.tr.serializer.instancers.ProcessParamsInstancer;
import me.tr.serializer.processes.serializer.Serializer;
import me.tr.serializer.types.GenericType;

import java.util.HashMap;
import java.util.Map;

public class RecordHandler implements TypeHandler {
    private Process process;

    public RecordHandler(Process process) {
        this.process = process;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        if (obj instanceof Map<?, ?> map) {
            return new ProcessParamsInstancer(getProcess(), (Map<String, Object>) map).instance(type.getClazz());
        }
        return obj;
    }

    @Override
    public Map<String, Object> serialize(Object obj, GenericType<?> type) {
        return ((Serializer) getProcess()).serialize(new HashMap<>(), obj);
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
