package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.processes.process.addons.PHandlerAddon;
import me.tr.trserializer.types.GenericType;

public class SHandlerAddon extends PHandlerAddon {

    @Override
    protected Object execute(TypeHandler handler, Object obj, GenericType<?> type) {
        return handler.serialize(obj, type);
    }

    @Override
    protected Class<?> getClassForHandler(Object obj, GenericType<?> type) {
        return obj == null ? Object.class : obj.getClass();
    }
}
