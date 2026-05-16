package me.tr.trserializer.registries;

import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.util.Optional;

public class SerializerHandlers extends CollectionRegistry<SerializerHandler> {
    private static final SerializerHandlers INSTANCE = new SerializerHandlers();

    private SerializerHandlers() {
    }

    public static SerializerHandlers getInstance() {
        return INSTANCE;
    }

    public static Optional<SerializerHandler> getHandlerFor(SerializerTask task) {
        return getInstance().find((h) -> h.canHandle(task));
    }
}
