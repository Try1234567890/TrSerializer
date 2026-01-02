package me.tr.serializer.utility;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    private static final Map<Class<?>, Class<?>> WRAPPERS = Map.of(
            Byte.class, byte.class,
            Character.class, char.class,
            Integer.class, int.class,
            Double.class, double.class,
            Float.class, float.class,
            Long.class, long.class,
            Short.class, short.class,
            Boolean.class, boolean.class
    );

    private Utility() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility classes.");
    }

    /**
     * Checks if the provided object is a wrapper class.
     *
     * @param obj The object to check.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    public static boolean isWrapper(Object obj) {
        return obj != null && WRAPPERS.containsKey(obj.getClass());
    }

    /**
     * Checks if the provided class is a wrapper class.
     *
     * @param clazz The class to check.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    public static boolean isWrapper(Class<?> clazz) {
        return clazz != null && WRAPPERS.containsKey(clazz);
    }

    /**
     * Retrieve the primitive class of the provided wrapper.
     *
     * @param clazz The wrapper to get primitive class of.
     * @return The primitive class if exists, otherwise {@code null}.
     */
    public static Class<?> getPrimitive(Class<?> clazz) {
        if (clazz.isPrimitive())
            return clazz;

        return WRAPPERS.get(clazz);
    }

    /**
     * Retrieve the wrapper of the provided primitive class.
     *
     * @param clazz The primitive class to get wrapper of.
     * @return The wrapper class if exists, otherwise {@code null}.
     */
    public static Class<?> getWrapper(Class<?> clazz) {
        if (isWrapper(clazz))
            return clazz;

        return WRAPPERS.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(clazz))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }


    /**
     * Retrieve the type of the list.
     *
     * @param coll The collection to get type of.
     * @return The class of the first object found, if the list is null or empty {@code null}.
     */
    public static Class<?> getType(Collection<?> coll) {
        if (coll == null || coll.isEmpty())
            return Object.class;
        return coll.iterator().next().getClass();
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
     * Get the generic of the provided field, if it has any.
     *
     * @param field The field to get generic from.
     * @return The class of the generics if process ends successfully, otherwise {@code null}.
     * @throws ClassNotFoundException if the class is not found.
     */
    public static Class<?> getGeneric(Field field) throws ClassNotFoundException {
        if (field == null)
            return null;

        Type generic = field.getGenericType();

        if (generic instanceof ParameterizedType par) {
            String className = par.getTypeName();
            if (hasGeneric(className))
                className = retrieveGeneric(className);
            return Class.forName(className, true, ClassLoader.getSystemClassLoader());
        }

        return null;
    }

    /**
     * Get the generic of the provided field, if it has any.
     *
     * @param field The field to get generic from.
     * @return The class of the generics if process ends successfully, otherwise {@code null}.
     */
    public static Class<?> getSafeGeneric(Field field) {
        if (field == null)
            return null;

        Type generic = field.getGenericType();

        if (generic instanceof ParameterizedType par) {
            try {
                String className = par.getTypeName();
                if (hasGeneric(className))
                    className = retrieveGeneric(className);
                return Class.forName(className, true, ClassLoader.getSystemClassLoader());
            } catch (ClassNotFoundException ignored) {
            }
        }

        return null;
    }

    /**
     * Checks if the provided class name has generics.
     *
     * @param name The class name.
     * @return {@code true} if it has, otherwise {@code false}.
     */
    public static boolean hasGeneric(String name) {
        return name.contains("<") && name.contains(">");
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
