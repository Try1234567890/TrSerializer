package me.tr.trserializer.registries;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.handlers.collection.ArrayHandler;
import me.tr.trserializer.handlers.collection.CollectionHandler;
import me.tr.trserializer.handlers.collection.MapHandler;
import me.tr.trserializer.handlers.java.*;
import me.tr.trserializer.processes.Process;
import me.tr.trserializer.utility.Utility;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;


public class HandlersRegistry extends Registry<Predicate<Class<?>>, Function<Process, TypeHandler>> {
    private static HandlersRegistry instance = new HandlersRegistry();
    private final Map<Predicate<Class<?>>, Function<Process, TypeHandler>> handlers = new LinkedHashMap<>();
    public static final UUIDHandler UUID_HANDLER = new UUIDHandler();
    public static final OptionalHandler OPTIONAL_HANDLER = new OptionalHandler();
    public static final StringHandler STRING_HANDLER = new StringHandler();
    public static final EnumHandler ENUM_HANDLER = new EnumHandler();
    public static final PrimitiveHandler PRIMITIVE_HANDLER = new PrimitiveHandler();

    public static HandlersRegistry getInstance() {
        if (instance == null) {
            instance = new HandlersRegistry();
        }
        return instance;
    }

    @Override
    public Map<Predicate<Class<?>>, Function<Process, TypeHandler>> getRegistry() {
        return handlers;
    }

    private HandlersRegistry() {
        handlers.put((c) -> c != null && (AtomicInteger.class.isAssignableFrom(c) ||
                AtomicLong.class.isAssignableFrom(c) ||
                AtomicBoolean.class.isAssignableFrom(c) ||
                AtomicReference.class.isAssignableFrom(c)), AtomicHandler::new);

        handlers.put((c) -> c != null && UUID.class.isAssignableFrom(c), (p) -> UUID_HANDLER);
        handlers.put((c) -> c != null && Collection.class.isAssignableFrom(c), CollectionHandler::new);
        handlers.put((c) -> c != null && Map.class.isAssignableFrom(c), MapHandler::new);
        handlers.put((c) -> c != null && Optional.class.isAssignableFrom(c), (p) -> OPTIONAL_HANDLER);
        handlers.put((c) -> c != null && String.class.isAssignableFrom(c), (p) -> STRING_HANDLER);
        handlers.put((c) -> c != null && c.isRecord(), RecordHandler::new);
        handlers.put((c) -> c != null && c.isArray(), ArrayHandler::new);
        handlers.put((c) -> c != null && c.isEnum(), (p) -> ENUM_HANDLER);
        handlers.put((c) -> c != null && c.isPrimitive() || Utility.isWrapper(c), (p) -> PRIMITIVE_HANDLER);
    }

    public TypeHandler get(Class<?> clazz, Process process) {

        for (Map.Entry<Predicate<Class<?>>, Function<Process, TypeHandler>> entry
                : getRegistry().entrySet()) {
            if (entry.getKey().test(clazz)) {
                return entry.getValue().apply(process);
            }
        }

        return null;

    }
}
