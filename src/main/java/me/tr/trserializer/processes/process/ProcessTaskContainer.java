package me.tr.trserializer.processes.process;

import me.tr.trserializer.types.GenericType;

import java.util.Map;

public record ProcessTaskContainer(Object obj, GenericType<?> type, Map<String, Object> map) {
}
