package me.tr.trserializer.processes.process.addons.date;

import me.tr.trserializer.handlers.dates.DateHandlerContainer;
import me.tr.trserializer.types.GenericType;

public interface PDateAnnMethods {

    Class<?> getClassForHandler(Object obj, GenericType<?> type);

    Object execute(DateHandlerContainer handler, Object obj, GenericType<?> type);

}
