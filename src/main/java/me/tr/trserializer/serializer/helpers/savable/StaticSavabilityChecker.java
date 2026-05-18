package me.tr.trserializer.serializer.helpers.savable;

import me.tr.trserializer.utility.Utility;
import me.tr.trserializer.utility.Wrappers;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * The savability checker is a system that checks if
 * the object provided is savable in serializing context.
 * <p>
 * In general an object is savable if is a collection, an array
 * or a map of primitives or wrappers or if it is a primitive or wrapper.
 * <p>
 * The {@link String} is considered as primitive type.
 * <p>
 * This implementation follow the singleton pattern and check for
 * savability with only the result object as information.
 */
public final class StaticSavabilityChecker implements SavabilityChecker {
    public static final StaticSavabilityChecker INSTANCE = new StaticSavabilityChecker();

    private StaticSavabilityChecker() {
    }

    /**
     * Checks if the object provided is savable in serializing context.
     * <p>
     * In general an object is savable if is a collection, an array
     * or a map of primitives or wrappers or if it is a primitive or wrapper.
     * <p>
     * The {@link String} is considered as primitive type.
     *
     * @param obj The object to check.
     * @return {@code true} if is savable, otherwise {@code false}.
     */
    public static boolean check(Object obj) {
        return INSTANCE.isSavable(obj);
    }

    /**
     * Checks if the object provided is savable in serializing context.
     * <p>
     * In general an object is savable if is a collection, an array
     * or a map of primitives or wrappers or if it is a primitive or wrapper.
     * <p>
     * The {@link String} is considered as primitive type.
     *
     * @param obj The object to check.
     * @return {@code true} if is savable, otherwise {@code false}.
     */
    @Override
    public boolean isSavable(Object obj) {
        if (obj == null) {
            // TODO: Add option to allow null value.
            return true;
        }

        Class<?> cls = obj.getClass();
        return switch (obj) {
            case Object arr when arr.getClass().isArray() -> isArraySavable(arr);
            case Collection<?> coll -> isSavable(coll);
            case Map<?, ?> map -> isSavable(map);
            default -> Wrappers.isPrimitiveOrWrapper(cls);
        };
    }

    private boolean isArraySavable(Object arr) {
        if (arr == null || !arr.getClass().isArray()) return false;
        if (Array.getLength(arr) == 0) return true;
        Object firstNonNull = Utility.getFirstArrayValueNonNull(arr);

        return isSavable(firstNonNull);
    }

    private boolean isSavable(Collection<?> coll) {
        if (coll.isEmpty()) return true;
        Object firstNonNull = Utility.getFirstValueNonNull(coll);

        return isSavable(firstNonNull);
    }

    private boolean isSavable(Map<?, ?> map) {
        if (map.isEmpty()) return true;
        Object firstNonNull = Utility.getFirstValueNonNull(map);

        return isSavable(firstNonNull);
    }
}
