package me.tr.trserializer.processes.process.addons.handler;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.logger.ProcessLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public class PHandlerAddon extends PAddon {
    private final PHandlerMethods methods;

    public PHandlerAddon(PHandlerMethods methods) {
        super("handlers");
        this.methods = methods;
    }

    public PHandlerMethods getMethods() {
        return methods;
    }

    public Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception {
        Optional<TypeHandler> handler = process.getHandler(getMethods().getClassForHandler(obj, type));

        if (handler.isPresent()) {
            ProcessLogger.dbg("Handler for " + type + " found.");
            return Optional.ofNullable(getMethods().execute(handler.get(), obj, type));
        }

        ProcessLogger.dbg("Handler for " + type + " not found.");
        return Optional.empty();
    }
}
