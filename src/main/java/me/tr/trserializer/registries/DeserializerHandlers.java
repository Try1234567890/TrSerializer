package me.tr.trserializer.registries;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.annotations.as.AsNumberAnnotationDeserializerHandler;
import me.tr.trserializer.deserializer.handlers.annotations.as.AsStringAnnotationDeserializerHandler;
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
        register(AsStringAnnotationDeserializerHandler.INSTANCE);
        register(AsNumberAnnotationDeserializerHandler.INSTANCE);
        register(ArrayDeserializerHandler.INSTANCE);
        register(CollectionDeserializerHandler.INSTANCE);
        register(MapDeserializerHandler.INSTANCE);
        register(MapEntryDeserializerHandler.INSTANCE);
        register(AtomicDeserializerHandler.INSTANCE);
        register(OptionalDeserializerHandler.INSTANCE);
        register(ReferenceDeserializerHandler.INSTANCE);
        register(CalendarDeserializerHandler.INSTANCE);
        register(DateDeserializerHandler.INSTANCE);
        register(DurationDeserializerHandler.INSTANCE);
        register(InstantDeserializerHandler.INSTANCE);
        register(LocalDateDeserializerHandler.INSTANCE);
        register(OffsetDateTimeDeserializerHandler.INSTANCE);
        register(SQLDateDeserializerHandler.INSTANCE);
        register(ZonedDateTimeDeserializerHandler.INSTANCE);
        register(BufferedInputStreamDeserializerHandler.INSTANCE);
        register(FileDeserializerHandler.INSTANCE);
        register(InputStreamDeserializerHandler.INSTANCE);
        register(PathDeserializerHandler.INSTANCE);
        register(URIDeserializerHandler.INSTANCE);
        register(URLDeserializerHandler.INSTANCE);
        register(ClassDeserializerHandler.INSTANCE);
        register(EnumDeserializerHandler.INSTANCE);
        register(BigDecimalDeserializerHandler.INSTANCE);
        register(BigIntegerDeserializerHandler.INSTANCE);
        register(UUIDDeserializerHandler.INSTANCE);
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
