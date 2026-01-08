package me.tr.trserializer.processes.serializer;

import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.ProcessTaskContainer;
import me.tr.trserializer.types.GenericType;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;

public class ISerializer extends Serializer {

    @Override
    public Map<String, Object> serializeAsMap(Object object, Map<String, Object> rootOfResult) {
        Deque<ProcessTaskContainer> tasks = new ArrayDeque<>();
        serialize(tasks, object, rootOfResult);

        return rootOfResult;
    }

    private void serialize(Deque<ProcessTaskContainer> tasks, Object rootObj, Map<String, Object> rootMap) {
        tasks.push(new ProcessTaskContainer(rootObj, new GenericType<>(Map.class), rootMap));

        while (!tasks.isEmpty()) {
            ProcessTaskContainer task = tasks.pop();

            super.serializeAsMap(task.obj(), task.map(), tasks);
        }
    }

    protected Optional<? extends ISerResult> result(Object... obj) {
        Optional<? extends RSerResult> result = super.result(obj);
        if (result.isEmpty()) return Optional.empty();

        RSerResult rResult = result.get();
        if (!(obj[4] instanceof Deque<?> tasks)) {
            TrLogger.warning("The param at index 4 is not a Deque. Switching to recursively serializer.");
            return super.result(obj).map(r -> new ISerResult(r, new ArrayDeque<>()));
        }

        Object peek = tasks.peek();
        if (!tasks.isEmpty() && !(peek instanceof ProcessTaskContainer)) {
            TrLogger.warning("The param at index 4 is not a deque of ProcessTaskContainer but: " + (peek == null ? "null" : peek.getClass().getName()) + ". Switching to recursively serializer.");
            return super.result(obj).map(r -> new ISerResult(r, new ArrayDeque<>()));
        }

        //noinspection unchecked
        return Optional.of(new ISerResult(rResult, (Deque<ProcessTaskContainer>) tasks));

    }
}

















