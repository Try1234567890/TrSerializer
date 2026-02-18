package me.tr.trserializer.processes.deserializer.addons;

import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.addons.handler.PHandlerAddon;
import me.tr.trserializer.processes.process.addons.handler.PHandlerMethods;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public class DHandlerAddon extends DAddon {
    private final PHandlerAddon handlers;

    public DHandlerAddon() {
        super("handlers");
        this.handlers = new PHandlerAddon(new PHandlerMethods() {
            @Override
            public Class<?> getClassForHandler(Object obj, GenericType<?> type) {
                return type.getTypeClass();
            }

            @Override
            public Object execute(TypeHandler handler, Object obj, GenericType<?> type) {
                return handler.deserialize(obj, type);
            }
        });
    }

    @Override
    public Optional<Object> process(Deserializer deserializer, Object obj, GenericType<?> type, Field field) throws ProcessError {
        return handlers.process(deserializer, obj, type, field);
    }
}
