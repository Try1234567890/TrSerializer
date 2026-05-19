package me.tr.trserializer.serializer.helpers.typeResolver;

import me.tr.trserializer.translator.FieldTask;
import me.tr.trserializer.types.GenericType;

/**
 * A serializer type resolver is a system that automatically
 * resolve the output object of a serializing process.
 * <p>
 * This implementation uses the provided {@link FieldTask} to
 * and provide an output type based not only on the object that
 * the serializer is processing but external information from the
 * provided task too. For example @Annotations on the field.
 */
public final class SerializerFieldTaskTypeResolver implements SerializerTypeResolver {
    private final FieldTask task;

    public SerializerFieldTaskTypeResolver(FieldTask task) {
        if (task == null) throw new IllegalArgumentException("The task cannot be null.");

        this.task = task;
    }

    public FieldTask getTask() {
        return task;
    }

    public GenericType<?> getType() {
        return getType(task.getObject());
    }

    @Override
    public GenericType<?> getType(Object obj) {
        // TODO: Add field filter
        return StaticSerializerTypeResolver.resolve(obj);
    }


}













