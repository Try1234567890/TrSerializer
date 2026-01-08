package me.tr.trserializer.processes.process;

import me.tr.trserializer.types.GenericType;

import java.util.Map;

public record ProcessTaskContainer(Object target, GenericType<?> type, Map<String, Object> dataMap) {

    public Object getInstance() {
        return target;
    }

    public Map<String, Object> getData() {
        return dataMap;
    }

    public Object getObject() {
        return target;
    }

    public Map<String, Object> getResultMap() {
        return dataMap;
    }
}