package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.processes.process.addons.handler.PHandlerAddon;
import me.tr.trserializer.processes.process.addons.handler.PHandlerMethods;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public class SHandlerAddon extends SAddon {
    private final PHandlerAddon handlers;

    public SHandlerAddon() {
        super("handlers");
        this.handlers = new PHandlerAddon(new PHandlerMethods() {
            @Override
            public Class<?> getClassForHandler(Object obj, GenericType<?> type) {
                return obj == null ? Object.class : obj.getClass();
            }

            @Override
            public Object execute(TypeHandler handler, Object obj, GenericType<?> type) {
                return handler.serialize(obj, type);
            }
        });
    }

    @Override
    public Optional<Object> process(Serializer process, Object obj, GenericType<?> type, Field field) throws ProcessError {
        return handlers.process(process, obj, type, field);
    }
}
