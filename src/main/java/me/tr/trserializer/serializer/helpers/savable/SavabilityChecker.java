package me.tr.trserializer.serializer.helpers.savable;

/**
 * The savability checker is a system that checks if
 * the object provided is savable in serializing context.
 * <p>
 * In general an object is savable if is a collection, an array
 * or a map of primitives or wrappers or if it is a primitive or wrapper.
 * <p>
 * The {@link String} is considered as primitive type.
 */
public interface SavabilityChecker {
    /**
     * Checks if the object provided is savable in serializing context.
     * <p>
     * In general an object is savable if is a collection, an array
     * or a map of primitives or wrappers or if it is a primitive or wrapper.
     * <p>
     * The {@link String} is considered as primitive type.
     *
     * @param result The process result to check for savability.
     * @return {@code true} if the object is savable, otherwise {@code false}.
     */
    boolean isSavable(Object result);
}
