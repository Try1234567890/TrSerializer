package me.tr.trserializer.deserializer.helper.typeResolver;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.translator.FieldTask;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
/**
 * A deserializer type resolver is a system that automatically
 * resolve the output object of a serializing process.
 * <p>
 * This implementation uses the provided {@link FieldTask} to
 * and provide an output type based not only on the object that
 * the deserializer is processing but external information from the
 * provided task too. For example @Annotations on the field.
 */
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
