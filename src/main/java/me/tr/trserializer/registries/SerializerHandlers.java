package me.tr.trserializer.registries;

import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.ArraySerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.CollectionSerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.MapSerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.MapEntrySerializerHandler;
import me.tr.trserializer.serializer.handlers.containers.AtomicSerializerHandler;
import me.tr.trserializer.serializer.handlers.containers.OptionalSerializerHandler;
import me.tr.trserializer.serializer.handlers.containers.ReferenceSerializerHandler;
import me.tr.trserializer.serializer.handlers.dates.*;
import me.tr.trserializer.serializer.handlers.files.*;
import me.tr.trserializer.serializer.handlers.internal.ClassSerializerHandler;
import me.tr.trserializer.serializer.handlers.internal.EnumSerializerHandler;
import me.tr.trserializer.serializer.handlers.numbers.BigDecimalSerializerHandler;
import me.tr.trserializer.serializer.handlers.numbers.BigIntegerSerializerHandler;
import me.tr.trserializer.serializer.handlers.security.UUIDSerializerHandler;

import java.util.Optional;


/**
 * This registry contains all the serialization
 * handlers.
 * <p>
 * If a serialization handler is not registered here
 * any serializer cannot use it.
 */
// TODO: Make the singleton pattern thread-safe
public class SerializerHandlers extends CollectionRegistry<SerializerHandler> {
    private static SerializerHandlers INSTANCE;

    private SerializerHandlers() {
        // Register default serializer handler
        register(new ArraySerializerHandler());
        register(new CollectionSerializerHandler());
        register(new MapSerializerHandler());
        register(new MapEntrySerializerHandler());
        register(new AtomicSerializerHandler());
        register(new OptionalSerializerHandler());
        register(new ReferenceSerializerHandler());
        register(new CalendarSerializerHandler());
        register(new DateSerializerHandler());
        register(new DurationSerializerHandler());
        register(new InstantSerializerHandler());
        register(new LocalDateSerializerHandler());
        register(new OffsetDateTimeSerializerHandler());
        register(new SQLDateSerializerHandler());
        register(new ZonedDateTimeSerializerHandler());
        register(new BufferedInputStreamSerializerHandler());
        register(new FileSerializerHandler());
        register(new InputStreamSerializerHandler());
        register(new PathSerializerHandler());
        register(new URISerializerHandler());
        register(new URLSerializerHandler());
        register(new ClassSerializerHandler());
        register(new EnumSerializerHandler());
        register(new BigDecimalSerializerHandler());
        register(new BigIntegerSerializerHandler());
        register(new UUIDSerializerHandler());
    }

    public static SerializerHandlers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SerializerHandlers();
        }
        return INSTANCE;
    }

    public static Optional<SerializerHandler> getHandlerFor(SerializerTask task) {
        return getInstance().find((h) -> h.canHandle(task));
    }
}
