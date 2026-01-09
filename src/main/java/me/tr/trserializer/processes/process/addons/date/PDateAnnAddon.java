package me.tr.trserializer.processes.process.addons.date;

import me.tr.trserializer.annotations.Format;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.handlers.dates.DateHandlerContainer;
import me.tr.trserializer.logger.Logger;
import me.tr.trserializer.logger.ProcessLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.processes.process.addons.Priority;
import me.tr.trserializer.processes.process.addons.handler.PHandlerMethods;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public class PDateAnnAddon extends PAddon {
    private final PDateAnnMethods methods;

    public PDateAnnAddon(PDateAnnMethods methods) {
        super("date-annotation", Priority.NORMAL_PLUS);
        this.methods = methods;
    }

    public PDateAnnMethods getMethods() {
        return methods;
    }

    @Override
    public Optional<Object> process(Process process, Object obj,
                                    GenericType<?> type, Field field) throws Exception {
        if (field == null) {
            Logger.dbg("The field is null, stopping execution of @Format addon.");
            return Optional.empty();
        }

        if (field.isAnnotationPresent(Format.class)) {
            Optional<TypeHandler> handlerOpt = process.getHandler(getMethods().getClassForHandler(obj, type));

            if (handlerOpt.isEmpty()) {
                ProcessLogger.dbg("No handler found for " + type.getTypeClass());
                return Optional.empty();
            }

            TypeHandler handler = handlerOpt.get();

            if (!(handler instanceof DateHandlerContainer date)) {
                ProcessLogger.dbg("The found handler is not an instance of DateHandler.");
                return Optional.empty();
            }

            Format ann = field.getAnnotation(Format.class);

            return Optional.ofNullable(getMethods().execute(date.format(ann.format()).timestamp(ann.timestamp()), obj, type));
        }

        return Optional.empty();
    }
}





















