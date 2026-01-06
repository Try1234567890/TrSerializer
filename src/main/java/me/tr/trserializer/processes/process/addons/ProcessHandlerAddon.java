package me.tr.trserializer.processes.process.addons;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.types.GenericType;

import java.util.Map;
import java.util.Optional;

public abstract class ProcessHandlerAddon extends ProcessAddon {

    public ProcessHandlerAddon() {
        super("Handler");
    }

    public Optional<Object> process(Process process, Object obj, GenericType<?> type) throws Exception {
        Optional<TypeHandler> handlerOpt = process.getHandler(getClassForHandler(obj, type));

        TrLogger.dbg("Handler for " + type + ": ");

        if (handlerOpt.isPresent()) {
            TrLogger.dbg("Handler found: ");

            TypeHandler handler = handlerOpt.get();

            if (process.getRunningHandlers().containsKey(handler)) {
                Map.Entry<Object, GenericType<?>> handlersValue =
                        process.getRunningHandlers().get(handler);

                if (handlersValue.getKey().equals(obj)
                        && handlersValue.getValue().equals(type)) {
                    TrLogger.dbg("The handler is already running, probable infinite recursion. Skipping...");
                    /*
                     * Is an already running handler that call this function.
                     * To prevent stack overflow error, we return Optional.empty()
                     * to proceed the execution in serialize () method.
                     */
                    return Optional.empty();
                }
            }

                return Optional.ofNullable(execute(handler, obj, type));
        }

        TrLogger.dbg("No handler found.");
        return Optional.empty();
    }

    protected abstract Class<?> getClassForHandler(Object obj, GenericType<?> type);

    protected abstract Object execute(TypeHandler handler, Object obj, GenericType<?> type);
}
