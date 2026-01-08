package me.tr.trserializer.processes.serializer;

import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.ProcessTaskContainer;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;

public class ISerializer extends Serializer {

    public <T> T serialize(Object obj, GenericType<T> type, Deque<ProcessTaskContainer> tasks) {
        return super.serialize(obj, type, tasks);
    }

    public Map<String, Object> serializeAsMap(Object obj, Map<String, Object> result, Deque<ProcessTaskContainer> tasks) {
        return super.serializeAsMap(obj, result, tasks);
    }

    public void serialize(Field field, Object instance,
                          Class<?> clazz, Map<String, Object> result,
                          Deque<ProcessTaskContainer> tasks) {
        super.serialize(field, instance, clazz, result, tasks);
    }

    public void serialize(Object rootObj, Map<String, Object> rootMap, Deque<ProcessTaskContainer> tasks) {
        tasks.push(new ProcessTaskContainer(rootObj, new GenericType<>(Map.class), rootMap));

        while (!tasks.isEmpty()) {
            ProcessTaskContainer task = tasks.pop();

            super.serializeAsMap(task.getObject(), task.getResultMap(), tasks);
        }
    }

    @Override
    public Map<String, Object> serializeAsMap(Object object, Map<String, Object> rootOfResult) {
        Deque<ProcessTaskContainer> tasks = new ArrayDeque<>();

        serialize(object, rootOfResult, tasks);

        return rootOfResult;
    }

    protected Optional<? extends ISerResult> result(Object... obj) {
        Optional<? extends RSerResult> result = super.result(obj);
        if (result.isEmpty()) return Optional.empty();

        RSerResult rResult = result.get();
        if (!(obj[4] instanceof Deque<?> tasks)) {
            TrLogger.dbg("The param at index 4 is not a Deque. Creating a new one.");
            return result.map(r -> new ISerResult(r, new ArrayDeque<>()));
        }

        Object peek = tasks.peek();
        if (!tasks.isEmpty() && !(peek instanceof ProcessTaskContainer)) {
            TrLogger.dbg("The param at index 4 is not a deque of ProcessTaskContainer but: " + (peek == null ? "null" : peek.getClass().getName()) + ". Creating a new one.");
            return result.map(r -> new ISerResult(r, new ArrayDeque<>()));
        }

        //noinspection unchecked
        return Optional.of(new ISerResult(rResult, (Deque<ProcessTaskContainer>) tasks));
    }
}

















