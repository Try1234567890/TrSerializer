package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.handlers.dates.DateHandlerContainer;
import me.tr.trserializer.processes.process.addons.ProcessDateAnnotationAddon;
import me.tr.trserializer.types.GenericType;

public class SerializerDateAnnotationAddon extends ProcessDateAnnotationAddon {

    @Override
    protected Object execute(DateHandlerContainer handler, Object obj, GenericType<?> type) {
        return handler.serialize(obj, type);
    }

    @Override
    protected Class<?> getClassForHandler(Object obj, GenericType<?> type) {
        return obj == null ? Object.class : obj.getClass();
    }
}





















