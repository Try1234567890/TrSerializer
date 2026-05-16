package me.tr.trserializer.utility;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class Utility {
    private static final Map<Class<?>, Class<?>> WRAPPERS_PRIMITIVE = Map.of(
            Byte.class, byte.class,
            Character.class, char.class,
            Integer.class, int.class,
            Double.class, double.class,
            Float.class, float.class,
            Long.class, long.class,
            Short.class, short.class,
            Boolean.class, boolean.class
    );

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = Map.of(
            byte.class, Byte.class,
            char.class, Character.class,
            int.class, Integer.class,
            double.class, Double.class,
            float.class, Float.class,
            long.class, Long.class,
            short.class, Short.class,
            boolean.class, Boolean.class
    );


    private Utility() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility classes.");
    }

    public static Object getFirstValueNonNull(Object[] arr) {
        if (arr.length == 0) return null;
        Object firstNonNull = arr[0];

        int index = 1;
        while (firstNonNull == null && (index < arr.length)) {
            firstNonNull = arr[index++];
        }

        return firstNonNull;
    }

    public static Object getFirstValueNonNull(Collection<?> coll) {
        if (coll.isEmpty()) return null;
        Object firstNonNull = coll.iterator().next();

        while (firstNonNull == null && !coll.isEmpty()) {
            firstNonNull = coll.iterator().next();
        }

        return firstNonNull;
    }

    public static Object getFirstKeyNonNull(Map<?, ?> map) {
        if (map.isEmpty()) return null;

        Iterator<?> keys = map.keySet().iterator();
        Object firstKeyNonNull = keys.next();

        while (firstKeyNonNull == null && !map.isEmpty()) {
            firstKeyNonNull = keys.next();
        }

        return firstKeyNonNull;
    }

    public static Object getFirstValueNonNull(Map<?, ?> map) {
        if (map.isEmpty()) return null;

        Iterator<?> values = map.values().iterator();
        Object firstValuesNonNull = values.next();

        while (firstValuesNonNull == null && !map.isEmpty()) {
            firstValuesNonNull = values.next();
        }

        return firstValuesNonNull;
    }

    /**
     * Checks if the provided class is a wrapper class.
     *
     * @param clazz The class to check.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    public static boolean isWrapper(Class<?> clazz) {
        return clazz != null && WRAPPERS_PRIMITIVE.containsKey(clazz);
    }

    public static Class<?> getWrapper(Class<?> clazz) {
        if (PRIMITIVE_WRAPPERS.containsKey(clazz)) {
            return PRIMITIVE_WRAPPERS.get(clazz);
        }
        return clazz;
    }

    public static String getClassName(Object object) {
        if (object == null) {
            SLogger.LOGGER.debug("Cannot retrieve class name because is null.");
            return "Unknown";
        }

        String name = object.getClass().getName().replace("class ", "");
        return switch (object) {
            case Object[] arr -> "Array of " + arr.getClass().getComponentType().getName();
            case Collection<?> coll -> "Collection of " + getClassName(coll);
            case Map<?, ?> map -> "Map of " + getKeyType(map) + " - " + getValueType(map);
            default -> name;
        };
    }

    public static boolean isAMapWithStringKeys(Object obj) {
        return isAMapWithStringKeys(obj, false);
    }

    public static boolean isAMapWithStringKeys(Object obj, boolean ignoreIfEmpty) {
        if (!(obj instanceof Map<?, ?> unsafeSubMap)) {
            SLogger.LOGGER.debug("Cannot verify the map key type because the provided object is not a map.");
            return false;
        }
        Class<?> keyCls = Utility.getKeyType(unsafeSubMap);
        return (ignoreIfEmpty && unsafeSubMap.isEmpty()) || (keyCls != null && String.class.isAssignableFrom(keyCls));
    }

    /**
     * Retrieve the type of the map keys.
     *
     * @param map The map to retrieve keys value from.
     * @return The class of the first key found, if the map is null or empty {@code null}.
     */
    public static Class<?> getKeyType(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            SLogger.LOGGER.debug("Cannot retrieve the map key type because is " + (map == null ? "null" : "empty") + ".");
            return null;
        }

        Object firstKey = map.keySet().iterator().next();
        return firstKey != null ? firstKey.getClass() : null;
    }

    public static Class<?> getValueType(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            SLogger.LOGGER.debug("Cannot retrieve the map value type because is " + (map == null ? "null" : "empty") + ".");
            return null;
        }

        Object firstKey = map.values().iterator().next();
        return firstKey != null ? firstKey.getClass() : null;
    }

    /**
     * Retrieve the type of the list values.
     *
     * @param coll The collection to retrieve value from.
     * @return The class of the first key found, if the map is null or empty {@code null}.
     */
    public static Class<?> getType(Collection<?> coll) {
        if (coll == null || coll.isEmpty()) {
            SLogger.LOGGER.debug("The collection is empty, cannot retrieve content type.");
            return null;
        }
        return coll.iterator().next().getClass();
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
