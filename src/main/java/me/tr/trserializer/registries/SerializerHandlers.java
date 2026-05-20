package me.tr.trserializer.registries;

import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.annotations.as.AsNumberAnnotationSerializerHandler;
import me.tr.trserializer.serializer.handlers.annotations.as.AsStringAnnotationSerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.ArraySerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.CollectionSerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.MapEntrySerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.MapSerializerHandler;
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
        register(AsStringAnnotationSerializerHandler.INSTANCE);
        register(AsNumberAnnotationSerializerHandler.INSTANCE);
        register(ArraySerializerHandler.INSTANCE);
        register(CollectionSerializerHandler.INSTANCE);
        register(MapSerializerHandler.INSTANCE);
        register(MapEntrySerializerHandler.INSTANCE);
        register(AtomicSerializerHandler.INSTANCE);
        register(OptionalSerializerHandler.INSTANCE);
        register(ReferenceSerializerHandler.INSTANCE);
        register(CalendarSerializerHandler.INSTANCE);
        register(DateSerializerHandler.INSTANCE);
        register(DurationSerializerHandler.INSTANCE);
        register(InstantSerializerHandler.INSTANCE);
        register(LocalDateSerializerHandler.INSTANCE);
        register(OffsetDateTimeSerializerHandler.INSTANCE);
        register(SQLDateSerializerHandler.INSTANCE);
        register(ZonedDateTimeSerializerHandler.INSTANCE);
        register(BufferedInputStreamSerializerHandler.INSTANCE);
        register(FileSerializerHandler.INSTANCE);
        register(InputStreamSerializerHandler.INSTANCE);
        register(PathSerializerHandler.INSTANCE);
        register(URISerializerHandler.INSTANCE);
        register(URLSerializerHandler.INSTANCE);
        register(ClassSerializerHandler.INSTANCE);
        register(EnumSerializerHandler.INSTANCE);
        register(BigDecimalSerializerHandler.INSTANCE);
        register(BigIntegerSerializerHandler.INSTANCE);
        register(UUIDSerializerHandler.INSTANCE);
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
