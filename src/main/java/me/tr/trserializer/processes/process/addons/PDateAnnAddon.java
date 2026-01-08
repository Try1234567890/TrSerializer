package me.tr.trserializer.processes.process.addons;

import me.tr.trserializer.annotations.Format;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.handlers.dates.DateHandlerContainer;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public abstract class PDateAnnAddon extends PAddon {


    public PDateAnnAddon() {
        super("date-annotation", Priority.NORMAL_PLUS);
    }

    @Override
    public Optional<Object> process(Process process, Object obj,
                                    GenericType<?> type, Field field) throws Exception {
        if (field == null)
            return Optional.empty();

        if (field.isAnnotationPresent(Format.class)) {
            Optional<TypeHandler> handlerOpt = process.getHandler(getClassForHandler(obj, type));

            if (handlerOpt.isEmpty()) {
                TrLogger.dbg("No handler found for " + type.getTypeClass());
                return Optional.empty();
            }

            TypeHandler handler = handlerOpt.get();

            if (!(handler instanceof DateHandlerContainer date)) {
                TrLogger.dbg("The found handler is not an instance of DateHandler.");
                return Optional.empty();
            }

            Format ann = field.getAnnotation(Format.class);

            return Optional.ofNullable(
                    execute(date.format(ann.format()).timestamp(ann.timestamp()), obj, type));
        }

        return Optional.empty();
    }


    protected abstract Class<?> getClassForHandler(Object obj, GenericType<?> type);

    protected abstract Object execute(DateHandlerContainer handler, Object obj, GenericType<?> type);


}





















