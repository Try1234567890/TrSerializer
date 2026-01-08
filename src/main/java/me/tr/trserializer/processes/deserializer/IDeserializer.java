package me.tr.trserializer.processes.deserializer;

import me.tr.trserializer.annotations.Aliases;
import me.tr.trserializer.annotations.Essential;
import me.tr.trserializer.annotations.unwrap.Unwrapped;
import me.tr.trserializer.annotations.wrap.Wrapped;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.ProcessTaskContainer;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Three;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("unchecked")
public class IDeserializer extends Deserializer {

    private void deserialize(Deque<ProcessTaskContainer> tasks, Object rootObj, Map<String, Object> values) {
        tasks.push(new ProcessTaskContainer(rootObj, new GenericType<>(Map.class), values));

        while (!tasks.isEmpty()) {
            ProcessTaskContainer task = tasks.pop();

            super.deserializeFromMap(task.getInstance(), task.getData(), tasks);
        }
    }

    @Override
    public Object deserializeFromMap(Object object, Map<String, Object> values) {
        Deque<ProcessTaskContainer> tasks = new ArrayDeque<>();

        deserialize(tasks, object, values);

        return object;
    }

    protected Optional<? extends IDesResult> result(Object... obj) {
        Optional<? extends RDesResult> result = super.result(obj);
        if (result.isEmpty()) return Optional.empty();

        RDesResult rResult = result.get();
        if (!(obj[5] instanceof Deque<?> tasks)) {
            TrLogger.dbg("The param at index 4 is not a Deque. Creating a new one.");
            return super.result(obj).map(r -> new IDesResult(r, new ArrayDeque<>()));
        }

        Object peek = tasks.peek();
        if (!tasks.isEmpty() && !(peek instanceof ProcessTaskContainer)) {
            TrLogger.dbg("The param at index 4 is not a deque of ProcessTaskContainer but: " + (peek == null ? "null" : peek.getClass().getName()) + ". Creating a new one.");
            return super.result(obj).map(r -> new IDesResult(r, new ArrayDeque<>()));
        }

        //noinspection unchecked
        return Optional.of(new IDesResult(rResult, (Deque<ProcessTaskContainer>) tasks));

    }
}
