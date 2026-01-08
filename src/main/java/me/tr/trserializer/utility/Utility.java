package me.tr.trserializer.utility;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.logger.TrLogger;

import java.lang.reflect.Array;
import java.util.*;

public class Utility {
    public static final Map<Class<?>, Object> DEFAULTS = new LinkedHashMap<>(
            Map.ofEntries(
                    Map.entry(Byte.class, (byte) -1),
                    Map.entry(byte.class, (byte) -1),
                    Map.entry(Integer.class, -1),
                    Map.entry(int.class, -1),
                    Map.entry(Long.class, -1L),
                    Map.entry(long.class, -1L),
                    Map.entry(Double.class, -1.0),
                    Map.entry(double.class, -1.0),
                    Map.entry(Float.class, -1.0f),
                    Map.entry(float.class, -1.0f),
                    Map.entry(Short.class, (short) -1),
                    Map.entry(short.class, (short) -1),
                    Map.entry(Boolean.class, false),
                    Map.entry(boolean.class, false),
                    Map.entry(String.class, ""),
                    Map.entry(Array.class, new Object[0]),
                    Map.entry(Set.class, new HashSet<>()),
                    Map.entry(List.class, new ArrayList<>()),
                    Map.entry(Map.class, new HashMap<>()),
                    Map.entry(Optional.class, Optional.empty()),
                    Map.entry(Collection.class, new ArrayList<>())
            )
    );

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

    public static String getClassName(Class<?> clazz) {
        if (clazz == null) return "null";
        if (clazz.isArray())
            return "Array of " + clazz.getComponentType().getName();
        return clazz.getName();
    }


    public static boolean isAMapWithStringKeys(Object obj) {
        if (!(obj instanceof Map<?, ?> unsafeSubMap)) {
            TrLogger.exception(
                    new TypeMissMatched("The provided object is not a map but " + (obj == null ? "null" : obj.getClass())));
            return false;
        }

        if (!String.class.isAssignableFrom(Utility.getKeyType(unsafeSubMap))) {
            TrLogger.exception(
                    new TypeMissMatched("The provided map keys type is not String.class"));
            return false;
        }

        return true;
    }

    /**
     * Retrieve the type of the map keys.
     *
     * @param map The map to retrieve keys value from.
     * @return The class of the first key found, if the map is null or empty {@code null}.
     */
    public static Class<?> getKeyType(Map<?, ?> map) {
        if (map == null || map.isEmpty())
            return Object.class;
        return map.keySet().iterator().next().getClass();
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
        if (!hasGeneric(name))
            return "";

        int open = name.indexOf("<");
        return name.substring(0, open);
    }

    /**
     * Retrieve the generics found in the provided
     * class name. If not found an empty string will be returned.
     *
     * @param name The class name.
     * @return The complete class name if found, otherwise an empty string.
     */
    public static String retrieveGeneric(String name) {
        if (!hasGeneric(name))
            return "";

        int open = name.indexOf("<");
        int close = name.indexOf(">");
        return name.substring(open + 1, close);
    }
}
