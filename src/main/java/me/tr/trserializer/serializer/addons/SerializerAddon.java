package me.tr.trserializer.serializer.addons;

import me.tr.trserializer.serializer.SerializerTask;

public interface SerializerAddon {

    void serialize(SerializerTask task);

    boolean canHandle(SerializerTask task);

}
