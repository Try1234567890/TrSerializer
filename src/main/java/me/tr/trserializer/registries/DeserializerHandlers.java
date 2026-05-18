package me.tr.trserializer.registries;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.collections.ArrayDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.collections.CollectionDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.collections.MapDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.collections.MapEntryDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.containers.AtomicDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.containers.OptionalDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.containers.ReferenceDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.dates.*;
import me.tr.trserializer.deserializer.handlers.files.*;
import me.tr.trserializer.deserializer.handlers.internal.ClassDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.internal.EnumDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.numbers.BigDecimalDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.numbers.BigIntegerDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.security.UUIDDeserializerHandler;

import java.util.Optional;
/**
 * This registry contains all the deserialization
 * handlers.
 * <p>
 * If a deserialization handler is not registered here
 * any deserializer cannot use it.
 */
// TODO: Make the singleton pattern thread-safe
public class DeserializerHandlers extends CollectionRegistry<DeserializerHandler> {
    private static DeserializerHandlers INSTANCE;

    private DeserializerHandlers() {
        // Register default deserializer handler
        register(new ArrayDeserializerHandler());
        register(new CollectionDeserializerHandler());
        register(new MapDeserializerHandler());
        register(new MapEntryDeserializerHandler());
        register(new AtomicDeserializerHandler());
        register(new OptionalDeserializerHandler());
        register(new ReferenceDeserializerHandler());
        register(new CalendarDeserializerHandler());
        register(new DateDeserializerHandler());
        register(new DurationDeserializerHandler());
        register(new InstantDeserializerHandler());
        register(new LocalDateDeserializerHandler());
        register(new OffsetDateTimeDeserializerHandler());
        register(new SQLDateDeserializerHandler());
        register(new ZonedDateTimeDeserializerHandler());
        register(new BufferedInputStreamDeserializerHandler());
        register(new FileDeserializerHandler());
        register(new InputStreamDeserializerHandler());
        register(new PathDeserializerHandler());
        register(new URIDeserializerHandler());
        register(new URLDeserializerHandler());
        register(new ClassDeserializerHandler());
        register(new EnumDeserializerHandler());
        register(new BigDecimalDeserializerHandler());
        register(new BigIntegerDeserializerHandler());
        register(new UUIDDeserializerHandler());
    }

    public static DeserializerHandlers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DeserializerHandlers();
        }
        return INSTANCE;
    }

    public static Optional<DeserializerHandler> getHandlerFor(DeserializerTask task) {
        return getInstance().find((h) -> h.canHandle(task));
    }
}
