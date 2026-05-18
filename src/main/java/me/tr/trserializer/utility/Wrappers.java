package me.tr.trserializer.utility;

import java.util.Map;

/**
 * This is a utility class useful to retrieve the wrapper
 * of a primitive data type and vice versa.
 */
public class Wrappers {
    private Wrappers() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility classes.");
    }

    /**
     * Mapped Wrapper-Primitive
     */
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

    /**
     * Mapped Primitive-Wrapper
     */
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

    /**
     * Checks if one {@code first class} is assignable from one of the classes.
     * <p>
     * This method consider the Wrapper of a primitive the
     * same as the primitive.
     *
     * @param from The first class
     * @param to   The second class
     * @return {@code true} if the {@code first class} is assignable from the {@code second class}, otherwise {@code false}.
     */
    public static boolean isAssignableFrom(Class<?> from, Class<?>... to) {
        if (from == null || to == null) return false;
        Class<?> newFrom = getWrapper(from);
        for (Class<?> t : to) {
            if (t == null) continue;
            if (newFrom.isAssignableFrom(t)) return true;
        }
        return false;
    }

    /**
     * Checks if {@code first class} is assignable from the {@code second class}.
     * <p>
     * This method consider the Wrapper of a primitive the
     * same as the primitive.
     *
     * @param from The first class
     * @param to   The second class
     * @return {@code true} if the {@code first class} is assignable from the {@code second class}, otherwise {@code false}.
     */
    public static boolean isAssignableFrom(Class<?> from, Class<?> to) {
        if (from == null || to == null) return false;
        if (from == to) return true;
        Class<?> newFrom = getWrapper(from);
        Class<?> newTo = getWrapper(to);
        return newFrom.isAssignableFrom(newTo);
    }

    /**
     * Checks if one {@code first class} is assignable from one of the classes or vice versa.
     * <p>
     * This method consider the Wrapper of a primitive the
     * same as the primitive.
     *
     * @param from The first class
     * @param to   The second class
     * @return {@code true} if the {@code first class} is assignable from the {@code second class}, otherwise {@code false}.
     */
    public static boolean isAssignable(Class<?> from, Class<?>... to) {
        if (from == null || to == null) return false;
        Class<?> newFrom = getWrapper(from);
        for (Class<?> newTo : to) {
            if (newTo == null) continue;
            if (isAssignable(newFrom, newTo)) return true;
        }
        return false;
    }

    /**
     * Checks if one of the two classes is assignable from the other one.
     * <p>
     * This method consider the Wrapper of a primitive the
     * same as the primitive.
     *
     * @param from The first class
     * @param to   The second class
     * @return {@code true} if the {@code first class} is assignable from the {@code second class}, otherwise {@code false}.
     */
    public static boolean isAssignable(Class<?> from, Class<?> to) {
        if (from == null || to == null) return false;
        if (from == to) return true;
        Class<?> newFrom = getWrapper(from);
        Class<?> newTo = getWrapper(to);
        return newFrom.isAssignableFrom(newTo) || newTo.isAssignableFrom(newFrom);
    }

    /**
     * Checks if the {@code class} is a primitive or a wrapper.
     * <p>
     * {@link String} is considered a primitive class.
     *
     * @param cls The class to check.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    public static boolean isPrimitiveOrWrapper(Class<?> cls) {
        return isPrimitive(cls) || isWrapper(cls);
    }

    /**
     * Checks if the {@code class} is a wrapper.
     * <p>
     * {@link String} is not considered a wrapper class.
     *
     * @param cls The class to check.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    public static boolean isWrapper(Class<?> cls) {
        return WRAPPERS_PRIMITIVE.containsKey(cls);
    }

    /**
     * Checks if the {@code class} is a primitive type.
     * <p>
     * {@link String} is considered a primitive class.
     *
     * @param cls The class to check.
     * @return {@code true} if it is, otherwise {@code false}.
     */
    public static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls.equals(String.class);
    }

    /**
     * Retrieve the wrapper of the provided primitive class.
     * <p>
     * If the {@code class} is not a {@code primitive}, the {@code class} will be returned.
     *
     * @param cls The primitive class.
     * @return The wrapper class if found, otherwise the provided class.
     */
    public static Class<?> getWrapper(Class<?> cls) {
        if (cls == null || !cls.isPrimitive()) {
            SLogger.LOGGER.debug("The " + Utility.getClassName(cls) + " is null or not a primitive. Cannot get wrapper...");
            return cls;
        }
        return PRIMITIVE_WRAPPERS.get(cls);
    }

    /**
     * Retrieve the primitive of the provided wrapper class.
     * <p>
     * If the {@code class} is not a {@code wrapper}, the {@code class} will be returned.
     *
     * @param cls The wrapper class.
     * @return The primitive class if found, otherwise the provided class.
     */
    public static Class<?> getPrimitive(Class<?> cls) {
        if (cls == null || !isWrapper(cls)) {
            SLogger.LOGGER.debug("The " + Utility.getClassName(cls) + " is null or not a wrapper. Cannot get primitive...");
            return cls;
        }
        return WRAPPERS_PRIMITIVE.get(cls);
    }
}
