package me.tr.trserializer.registries;

import me.tr.trserializer.annotations.AsNumber;
import me.tr.trserializer.annotations.AsString;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.handlers.annotation.AsNumberHandler;
import me.tr.trserializer.handlers.annotation.AsStringHandler;
import me.tr.trserializer.handlers.collection.ArrayHandler;
import me.tr.trserializer.handlers.collection.CollectionHandler;
import me.tr.trserializer.handlers.collection.MapHandler;
import me.tr.trserializer.handlers.dates.DateHandler;
import me.tr.trserializer.handlers.dates.LocalDateHandler;
import me.tr.trserializer.handlers.dates.LocalDateTimeHandler;
import me.tr.trserializer.handlers.dates.SQLDateHandler;
import me.tr.trserializer.handlers.java.*;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.utility.Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public static final LocalDateTimeHandler LOCAL_DATE_TIME_HANDLER = new LocalDateTimeHandler();
    public static final LocalDateHandler LOCAL_DATE_HANDLER = new LocalDateHandler();
    public static final SQLDateHandler SQL_DATE_HANDLER = new SQLDateHandler();
    public static final DateHandler DATE_HANDLER = new DateHandler();
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
        handlers.put((c) -> c.isAnnotationPresent(AsString.class), AsStringHandler::new);
        handlers.put((c) -> c.isAnnotationPresent(AsNumber.class), AsNumberHandler::new);

        handlers.put((c) -> (AtomicInteger.class.isAssignableFrom(c) ||
                AtomicLong.class.isAssignableFrom(c) ||
                AtomicBoolean.class.isAssignableFrom(c) ||
                AtomicReference.class.isAssignableFrom(c)), AtomicHandler::new);

        handlers.put(LocalDateTime.class::isAssignableFrom, (p) -> LOCAL_DATE_TIME_HANDLER);
        handlers.put(LocalDate.class::isAssignableFrom, (p) -> LOCAL_DATE_HANDLER);
        handlers.put(Date.class::isAssignableFrom, (p) -> DATE_HANDLER);
        handlers.put(java.sql.Date.class::isAssignableFrom, (p) -> SQL_DATE_HANDLER);
        handlers.put(UUID.class::isAssignableFrom, (p) -> UUID_HANDLER);
        handlers.put(Collection.class::isAssignableFrom, CollectionHandler::new);
        handlers.put(Map.class::isAssignableFrom, MapHandler::new);
        handlers.put(Optional.class::isAssignableFrom, (p) -> OPTIONAL_HANDLER);
        handlers.put(String.class::isAssignableFrom, (p) -> STRING_HANDLER);
        handlers.put(Class::isRecord, RecordHandler::new);
        handlers.put(Class::isArray, ArrayHandler::new);
        handlers.put(Class::isEnum, (p) -> ENUM_HANDLER);
        handlers.put((c) -> c.isPrimitive() || Utility.isWrapper(c), (p) -> PRIMITIVE_HANDLER);
    }

    public Optional<TypeHandler> get(Class<?> clazz, Process process) {
        for (Map.Entry<Predicate<Class<?>>, Function<Process, TypeHandler>> entry
                : getRegistry().entrySet()) {
            if (clazz != null &&
                    entry.getKey().test(clazz)) {
                return Optional.ofNullable(entry.getValue().apply(process));
            }
        }

        return Optional.empty();
    }
}
