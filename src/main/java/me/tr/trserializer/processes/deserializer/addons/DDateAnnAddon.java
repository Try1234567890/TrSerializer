package me.tr.trserializer.processes.deserializer.addons;

import me.tr.trserializer.handlers.dates.DateHandlerContainer;
import me.tr.trserializer.processes.process.addons.PDateAnnAddon;
import me.tr.trserializer.types.GenericType;

public class DDateAnnAddon extends PDateAnnAddon {

    @Override
    protected Object execute(DateHandlerContainer handler, Object obj, GenericType<?> type) {
        return handler.deserialize(obj, type);
    }

    @Override
    protected Class<?> getClassForHandler(Object obj, GenericType<?> type) {
        return type.getTypeClass();
    }
}





















