package me.tr.trserializer.processes.process.addons.handler;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.types.GenericType;

public interface PHandlerMethods {

    Class<?> getClassForHandler(Object obj, GenericType<?> type);

    Object execute(TypeHandler handler, Object obj, GenericType<?> type);

}
