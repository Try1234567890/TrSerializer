package me.tr.trserializer.deserializer.helper.typeResolver;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;

public class DeserializerTaskTypeResolver implements DeserializerTypeResolver {
    private final DeserializerTask task;

    public DeserializerTaskTypeResolver(DeserializerTask task) {
        this.task = task;
    }

    public DeserializerTask getTask() {
        return task;
    }

    @Override
    public GenericType<?> getType(Field field) {
        // TODO: Adding options to task allowing
        //       the customization of a type
        //       (like "serialize all byte[] as String UTF-8").
        return StaticDeserializerTypeResolver.resolve(field);
    }
}
