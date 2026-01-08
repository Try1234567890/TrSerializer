package me.tr.trserializer.processes.process.addons;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.logger.ProcessLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

public abstract class PHandlerAddon extends PAddon {

    public PHandlerAddon() {
        super("handlers");
    }

    public Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception {
        Optional<TypeHandler> handler = process.getHandler(getClassForHandler(obj, type));

        if (handler.isPresent()) {
            ProcessLogger.dbg("Handler for " + type + " found.");
            return Optional.ofNullable(execute(handler.get(), obj, type));
        }

        ProcessLogger.dbg("Handler for " + type + " not found.");
        return Optional.empty();
    }

    protected abstract Class<?> getClassForHandler(Object obj, GenericType<?> type);

    protected abstract Object execute(TypeHandler handler, Object obj, GenericType<?> type);
}
