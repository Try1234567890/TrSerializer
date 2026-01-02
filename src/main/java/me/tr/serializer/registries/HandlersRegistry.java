package me.tr.serializer.registries;

import me.tr.serializer.annotations.AsNumber;
import me.tr.serializer.annotations.AsString;
import me.tr.serializer.handlers.*;
import me.tr.serializer.processes.Process;
import me.tr.serializer.utility.Utility;

import java.lang.ref.Reference;
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

    public static HandlersRegistry getInstance() {
        if (instance == null) {
            instance = new HandlersRegistry();
        }
        return instance;
    }

    /**
     * Get the {@code Map<I, V>} that contains all the values of the registry.
     *
     * @return the {@code Map<I, V>} that contains all the values of the registry.
     */
    @Override
    public Map<Predicate<Class<?>>, Function<Process, TypeHandler>> getRegistry() {
        return handlers;
    }

    private HandlersRegistry() {
        handlers.put((c) -> c != null && c.isAnnotationPresent(AsNumber.class), AsNumberHandler::new);
        handlers.put((c) -> c != null && c.isAnnotationPresent(AsString.class), AsStringHandler::new);

        handlers.put((c) -> c != null && (AtomicInteger.class.isAssignableFrom(c) || AtomicLong.class.isAssignableFrom(c) || AtomicBoolean.class.isAssignableFrom(c) || AtomicReference.class.isAssignableFrom(c)), AtomicHandler::new);
        handlers.put((c) -> c != null && Reference.class.isAssignableFrom(c), ReferenceHandler::new);
        handlers.put((c) -> c != null && UUID.class.isAssignableFrom(c), (p) -> new UUIDHandler());
        handlers.put((c) -> c != null && Collection.class.isAssignableFrom(c), CollectionHandler::new);
        handlers.put((c) -> c != null && Map.class.isAssignableFrom(c), MapHandler::new);
        handlers.put((c) -> c != null && Optional.class.isAssignableFrom(c), (p) -> new OptionalHandler());
        handlers.put((c) -> c != null && String.class.isAssignableFrom(c), (p) -> new StringHandler());
        handlers.put((c) -> c != null && c.isRecord(), RecordHandler::new);
        handlers.put((c) -> c != null && c.isArray(), ArrayHandler::new);
        handlers.put((c) -> c != null && c.isEnum(), (p) -> new EnumHandler());
        handlers.put((c) -> c != null && c.isPrimitive() || Utility.isWrapper(c), (p) -> new PrimitiveHandler());
    }

    public TypeHandler get(Class<?> clazz, Process process) {
        return handlers.entrySet()
                .stream()
                .filter(entry -> entry.getKey().test(clazz))
                .map(entry -> entry.getValue().apply(process))
                .findFirst()
                .orElse(null);

    }
}
