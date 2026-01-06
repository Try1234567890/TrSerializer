package me.tr.trserializer.processes.deserializer.addons;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.processes.process.addons.PHandlerAddon;
import me.tr.trserializer.types.GenericType;

public class DHandlerAddon extends PHandlerAddon {

    @Override
    protected Object execute(TypeHandler handler, Object obj, GenericType<?> type) {
        return handler.deserialize(obj, type);
    }

    @Override
    protected Class<?> getClassForHandler(Object obj, GenericType<?> type) {
        return type.getTypeClass();
    }
}
