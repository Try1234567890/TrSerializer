package me.tr.trserializer.serializer.helpers.savable;

import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.translator.FieldTask;

/**
 * The savability checker is a system that checks if
 * the object provided is savable in serializing context.
 * <p>
 * In general an object is savable if is a collection, an array
 * or a map of primitives or wrappers or if it is a primitive or wrapper.
 * <p>
 * The {@link String} is considered as primitive type.
 * <p>
 * This implementation use the provided {@link SerializerTask} to
 * provide extra information, excluded the result object,
 * to check for savability. For example class @Annotation or
 * original {@link me.tr.trserializer.translator.Translator#getOptions()}.
 */
public final class SerializerTaskSavabilityChecker implements SavabilityChecker {
    private final SerializerTask task;

    public SerializerTaskSavabilityChecker(SerializerTask task) {
        if (task == null) throw new IllegalArgumentException("The task cannot be null.");
        this.task = task;
    }

    /**
     * @return the task of this savability checker.
     */
    public SerializerTask getTask() {
        return task;
    }

    /**
     * Checks if the object provided is savable in serializing context.
     * <p>
     * In general an object is savable if is a collection, an array
     * or a map of primitives or wrappers or if it is a primitive or wrapper.
     * <p>
     * The {@link String} is considered as primitive type.
     */
    public boolean isSavable() {
        return isSavable(getTask().getObject());
    }

    /**
     * Checks if the object provided is savable in serializing context.
     * <p>
     * In general an object is savable if is a collection, an array
     * or a map of primitives or wrappers or if it is a primitive or wrapper.
     * <p>
     * The {@link String} is considered as primitive type.
     */
    @Override
    public boolean isSavable(Object object) {
        return StaticSavabilityChecker.check(object);
    }
}
