package me.tr.trserializer.utility;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;


public class Validator {
    private Validator() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility classes.");
    }


    /**
     * Checks if the object is null with {@link #isNull(Object)}
     * and thrown a NullPointerException (wrapped in a RuntimeException) if it is.
     *
     * @param object The object to check.
     */
    public static void checkNull(Object object) {
        checkNull(object, new NullPointerException("object is null"));
    }

    /**
     * Checks if the object is null with {@link #isNull(Object)}
     * and thrown a NullPointerException with the {@code msg}
     * (wrapped in a RuntimeException) if it is.
     *
     * @param object The object to check.
     * @param msg    The message to include with NullPointerExcpetion.
     */
    public static void checkNull(Object object, String msg) {
        checkNull(object, new NullPointerException(isNull(msg) ? "" : msg));
    }

    /**
     * Checks if the object is null with {@link #isNull(Object)}
     * and thrown the exception (wrapped in a RuntimeException) if it is.
     * <p>
     * If the {@code throwable} is null, no exception is thrown.
     *
     * @param object    The object to check.
     * @param throwable The exception to throw.
     */
    public static void checkNull(Object object, Throwable throwable) {
        if (isSimpleNull(throwable)) return;

        if (isNull(object)) {
            // Wrapping to RuntimeExeption.
            throw new RuntimeException("", throwable);
        }
    }

    /**
     * Checks if the object is considered null.
     * <p>
     * An object is considered null if at least one
     * of this condition is true:
     * <ol>
     *  <li>Object is null (so: {@code object == null})</li>
     *  <li>Object is an empty collection.</li>
     *  <li>Object is an empty array.</li>
     *  <li>Object is an empty optional.</li>
     *  <li>Object is an empty map.</li>
     *  <li>Object is an empty string.</li>
     * </ol>
     *
     * @param object The object to check
     * @return {@code true} if is null, otherwise {@code false}.
     * @see #isNull(Map)
     * @see #isNull(Optional)
     * @see #isNull(String)
     * @see #isNull(Collection)
     * @see #isNullArray(Object)
     * @see #isSimpleNull(Object)
     */
    public static boolean isNull(Object object) {
        return isSimpleNull(object) ||
                (object instanceof Collection<?> coll && isNull(coll)) ||
                (object.getClass().isArray() && isNullArray(object)) ||
                (object instanceof Optional<?> opt && isNull(opt)) ||
                (object instanceof Map<?, ?> map && isNull(map)) ||
                (object instanceof String str && isNull(str));
    }

    /**
     * Checks if the provided object is null. <p>
     * (same as {@code object == null})
     *
     * @param object The object to check
     * @return {@code true} if is null, otherwise {@code false}.
     * @see #isNull(Object)
     */
    public static boolean isSimpleNull(Object object) {
        return object == null;
    }

    /**
     * Checks if the {@code collection} is null or empty.
     *
     * @param collection The collection to check.
     * @return {@code true} if is null or empty, otherwise {@code false}.
     */
    public static boolean isNull(Collection<?> collection) {
        return isSimpleNull(collection) || collection.isEmpty();
    }


    /**
     * Checks if the {@code array} is null or empty.
     *
     * @param array The array to check.
     * @return {@code true} if is null, empty or is not an array, otherwise {@code false}.
     */
    public static boolean isNullArray(Object array) {
        if (isSimpleNull(array)) return true;
        if (!array.getClass().isArray()) return true;
        return Array.getLength(array) == 0;
    }

    /**
     * Checks if the {@code optional} is null or empty.
     *
     * @param optional The optional to check.
     * @return {@code true} if is null or empty, otherwise {@code false}.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static boolean isNull(Optional<?> optional) {
        return isSimpleNull(optional) || optional.isEmpty();
    }

    /**
     * Checks if the {@code map} is null or empty.
     *
     * @param map The map to check.
     * @return {@code true} if is null or empty, otherwise {@code false}.
     */
    public static boolean isNull(Map<?, ?> map) {
        return isSimpleNull(map) || map.isEmpty();
    }

    /**
     * Checks if the {@code string} is null or empty.
     *
     * @param string The string to check.
     * @return {@code true} if is null or empty, otherwise {@code false}.
     */
    public static boolean isNull(String string) {
        return isSimpleNull(string) || string.isEmpty();
    }

}
