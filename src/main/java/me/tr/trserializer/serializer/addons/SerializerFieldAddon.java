package me.tr.trserializer.serializer.addons;

import me.tr.trserializer.serializer.SerializerFieldTask;

public interface SerializerFieldAddon {

    void serialize(SerializerFieldTask task);

    boolean canHandle(SerializerFieldTask task);
}
