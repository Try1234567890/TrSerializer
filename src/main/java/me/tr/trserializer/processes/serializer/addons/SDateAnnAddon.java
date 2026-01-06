package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.handlers.dates.DateHandlerContainer;
import me.tr.trserializer.processes.process.addons.PDateAnnAddon;
import me.tr.trserializer.types.GenericType;

public class SDateAnnAddon extends PDateAnnAddon {

    @Override
    protected Object execute(DateHandlerContainer handler, Object obj, GenericType<?> type) {
        return handler.serialize(obj, type);
    }

    @Override
    protected Class<?> getClassForHandler(Object obj, GenericType<?> type) {
        return obj == null ? Object.class : obj.getClass();
    }
}





















