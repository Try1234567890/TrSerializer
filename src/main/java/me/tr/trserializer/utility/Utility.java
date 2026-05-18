package me.tr.trserializer.utility;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class Utility {
    private Utility() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility classes.");
    }

    /**
     * Retrieve the first value non-null of this array.
     *
     * @param arr The map.
     * @return The first value non-null, or {@code null} if is empty or all values are null.
     */
    public static Object getFirstValueNonNull(Object[] arr) {
        if (arr.length == 0) return null;

        Iterator<?> values = Arrays.stream(arr).iterator();
        return getFirstValueNonNull(values);
    }

    /**
     * Retrieve the first value non-null of this array.
     *
     * @param arr The map.
     * @return The first value non-null, or {@code null} if is empty or all values are null or is not an array.
     */
    public static Object getFirstArrayValueNonNull(Object arr) {
        if (arr == null || !arr.getClass().isArray()) return null;

        int len = Array.getLength(arr);
        if (len == 0) return null;

        Object firstKeyNonNull = Array.get(arr, 0);
        int index = 0;
        while (++index < len) {
            Object newFirstKeyNonNull = Array.get(arr, index);
            if (newFirstKeyNonNull == null) continue;
            if (!Wrappers.isAssignable(firstKeyNonNull.getClass(), newFirstKeyNonNull.getClass())) {
                firstKeyNonNull = new Object();
                break;
            }
        }

        return firstKeyNonNull;
    }

    /**
     * Retrieve the first value non-null of this map.
     *
     * @param coll The map.
     * @return The first value non-null, or {@code null} if is empty or all values are null.
     */
    public static Object getFirstValueNonNull(Collection<?> coll) {
        if (coll.isEmpty()) return null;

        Iterator<?> keys = coll.iterator();
        return getFirstValueNonNull(keys);
    }

    /**
     * Retrieve the first key non-null of this map.
     *
     * @param map The map.
     * @return The first key non-null, or {@code null} if is empty or all keys are null.
     */
    public static Object getFirstKeyNonNull(Map<?, ?> map) {
        if (map.isEmpty()) return null;

        Iterator<?> keys = map.keySet().iterator();
        return getFirstValueNonNull(keys);
    }


    /**
     * Retrieve the first value non-null of this map.
     *
     * @param map The map.
     * @return The first value non-null, or {@code null} if is empty or all values are null.
     */
    public static Object getFirstValueNonNull(Map<?, ?> map) {
        if (map.isEmpty()) return null;

        Iterator<?> values = map.values().iterator();
        return getFirstValueNonNull(values);
    }

    /**
     * Retrieve the first value non-null of this iterator.
     *
     * @param it The iterator.
     * @return The first value non-null, or {@code null} if is empty or all values are null.
     */
    public static Object getFirstValueNonNull(Iterator<?> it) {
        if (it == null || !it.hasNext()) return null;

        Object firstKeyNonNull = it.next();
        while (it.hasNext()) {
            Object newFirstKeyNonNull = it.next();
            if (newFirstKeyNonNull == null) continue;
            if (!Wrappers.isAssignable(firstKeyNonNull.getClass(), newFirstKeyNonNull.getClass())) {
                firstKeyNonNull = new Object();
                break;
            }
        }

        return firstKeyNonNull;
    }

    /**
     * <b>DEPRECATED: Use {@link Wrappers#isWrapper(Class)}</b>
     * <p>
     * Checks if the provided class is a wrapper class.
     *
     * @param clazz The class to check.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    @Deprecated
    public static boolean isWrapper(Class<?> clazz) {
        return Wrappers.isWrapper(clazz);
    }

    /**
     * Get an elegant class name useful for debug/info log.
     *
     * @param object The object to get the class name for.
     * @return An elegant class name.
     */
    public static String getClassName(Object object) {
        if (object == null) {
            SLogger.LOGGER.debug("Cannot retrieve class name because is null.");
            return "Unknown";
        }

        String name = object.getClass().getName().replace("class ", "");
        return switch (object) {
            case Object arr when arr.getClass().isArray() -> "Array of " + getClassName(Utility.getFirstArrayValueNonNull(arr));
            case Collection<?> coll -> "Collection of " + getClassName(getFirstValueNonNull(coll));
            case Map<?, ?> map -> "Map of " + getClassName(getFirstKeyNonNull(map)) + " - " + getClassName(getFirstValueNonNull(map));
            default -> name;
        };
    }

    /**
     * Retrieve the key type of the {@code map} if possibile, otherwise {@code null}.
     * <p>
     * Is not possibile to retrieve key type if the map is null or empty or if all keys are null.
     *
     * @param map The map to get key type from.
     * @return The map key type if possibile, otherwise the {@code null}.
     */
    public static Class<?> getKeyType(Map<?, ?> map) {
        return getKeyType(map, null);
    }

    /**
     * Retrieve the key type of the {@code map} if possibile.
     * <p>
     * Is not possibile to retrieve key type if the map is null or empty or if all keys are null.
     *
     * @param map The map to get key type from.
     * @param or  The alternative if key type cannot be retrieved.
     * @return The map key type if possibile, otherwise the alternative provided.
     */
    public static Class<?> getKeyType(Map<?, ?> map, Class<?> or) {
        if (map == null || map.isEmpty()) {
            SLogger.LOGGER.debug("Cannot retrieve the map key type because is " + (map == null ? "null" : "empty") + ".");
            return or;
        }

        Object firstKey = getFirstKeyNonNull(map);
        return firstKey != null ? firstKey.getClass() : or;
    }

    /**
     * Retrieve the value type of the {@code map} if possibile, otherwise {@code null}.
     * <p>
     * Is not possibile to retrieve value type if the map is null or empty or if all values are null.
     *
     * @param map The map to get value type from.
     * @return The map value type if possibile, otherwise the {@code null}.
     * @see #getValueType(Map, Class)
     */
    public static Class<?> getValueType(Map<?, ?> map) {
        return getValueType(map, null);
    }

    /**
     * Retrieve the value type of the {@code map} if possibile.
     * <p>
     * Is not possibile to retrieve value type if the map is null or empty or if all values are null.
     *
     * @param map The map to get value type from.
     * @param or  The alternative if value type cannot be retrieved.
     * @return The map value type if possibile, otherwise the alternative provided.
 */
public static Class<?> getValueType(Map<?, ?> map, Class<?> or) {
        if (map == null || map.isEmpty()) {
            SLogger.LOGGER.debug("Cannot retrieve the map value type because is " + (map == null ? "null" : "empty") + ".");
            return or;
        }

    Object firstValue = getFirstValueNonNull(map);
    return firstValue != null ? firstValue.getClass() : or;
}

    /**
     * Retrieve the type of the collection values.
     *
     * @param coll The collection to retrieve value from.
 * @return The class of the first key found, if the collection is null or empty {@code null}.
     */
    public static Class<?> getType(Collection<?> coll) {
        if (coll == null || coll.isEmpty()) {
            SLogger.LOGGER.debug("The collection is empty, cannot retrieve content type.");
            return null;
        }
        Object firstVal = getFirstValueNonNull(coll);
        return firstVal != null ? firstVal.getClass() : null;
    }

    /**
     * Checks if the provided class name has generics.
     *
     * @param name The class name.
     * @return {@code true} if it has, otherwise {@code false}.
     */
    public static boolean hasGeneric(String name) {
        return name != null && name.contains("<") && name.contains(">");
    }

    public static String removeGeneric(String name) {
        if (!hasGeneric(name)) {
            SLogger.LOGGER.debug("Class name '" + name + "' doesn't have generics.");
            return name;
        }

        int open = name.indexOf("<");
        return name.substring(0, open);
    }
}
