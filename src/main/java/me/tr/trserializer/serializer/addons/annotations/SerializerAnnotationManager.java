package me.tr.trserializer.serializer.addons.annotations;

import me.tr.trserializer.serializer.SerializerFieldTask;
import me.tr.trserializer.serializer.addons.SerializerFieldAddon;

public class SerializerAnnotationManager implements SerializerFieldAddon {

    @Override
    public void serialize(SerializerFieldTask task) {

    }

    @Override
    public boolean canHandle(SerializerFieldTask task) {
        return task.getField().getAnnotations().length != 0;
    }
}
