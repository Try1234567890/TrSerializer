package me.tr.trserializer.types;

import me.tr.trserializer.utility.SLogger;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Represents a generic type at runtime, encapsulating both the raw class
 * and its associated type arguments.
 * <p>
 * This class implements {@link ParameterizedType} to ensure full compatibility
 * with the standard Java Reflection API.
 * </p>
 *
 * @param <T> The main type, used for type safety at call sites.
 */
public class GenericType<T> implements ParameterizedType {
    private final Class<T> typeClass;
    private final Type[] typeArguments;

    /**
     * Constructs a new GenericType using a raw type and optional type arguments.
     *
     * @param rawType       The base type (e.g., {@code List.class}).
     * @param typeArguments The generic arguments (e.g., {@code String.class}).
     * @throws IllegalArgumentException If the raw type cannot be resolved to a class.
     */
    @SuppressWarnings("unchecked")
    public GenericType(Type rawType, Type... typeArguments) {
        this.typeClass = (Class<T>) resolveClass(rawType);
        this.typeArguments = (typeArguments != null) ? typeArguments.clone() : new Type[0];
    }

    /**
     * Infers the raw type and generic arguments from a specific {@link Field}.
     *
     * @param field The field to inspect.
     */
    @SuppressWarnings("unchecked")
    public GenericType(Field field) {
        this.typeClass = (Class<T>) field.getType();
        this.typeArguments = resolveTypeArguments(field);
    }

    // -------------------------------------------------------------------------
    // Factory Methods
    // -------------------------------------------------------------------------

    /**
     * Creates a GenericType for a simple class with type arguments.
     */
    public static <T> GenericType<T> of(Class<T> clazz, Type... typeArguments) {
        return new GenericType<>(clazz, typeArguments);
    }

    /**
     * Creates a GenericType for a simple class without type arguments.
     */
    public static <T> GenericType<T> of(Class<T> clazz) {
        return new GenericType<>(clazz);
    }

    /**
     * Creates a GenericType by inspecting the given field.
     */
    public static <T> GenericType<T> of(Field field) {
        return new GenericType<>(field);
    }

    // -------------------------------------------------------------------------
    // ParameterizedType Implementation
    // -------------------------------------------------------------------------

    /**
     * Returns the raw type (e.g., List, Map, or a custom class).
     *
     * @return The {@link Class} representing the raw type.
     */
    @Override
    public Type getRawType() {
        return typeClass;
    }

    /**
     * Returns the owner type for member classes (e.g., {@code Map} for {@code Map.Entry}).
     * Returns {@code null} for top-level classes.
     */
    @Override
    public Type getOwnerType() {
        return typeClass.isMemberClass() ? typeClass.getEnclosingClass() : null;
    }

    /**
     * Returns a copy of the actual type arguments.
     *
     * @return An array of {@link Type} objects.
     */
    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments.clone();
    }

    // -------------------------------------------------------------------------
    // Additional API
    // -------------------------------------------------------------------------

    /**
     * Returns the {@link Class} object associated with the raw type.
     */
    public Class<T> getTypeClass() {
        return typeClass;
    }

    /**
     * Returns the class of the first type argument.
     * * @return The first argument's class, or {@code Object.class} if none exist.
     */
    public Class<?> getFirstArgumentClass() {
        return getArgumentClass(0);
    }

    /**
     * Returns the class of the type argument at the specified index.
     *
     * @param index The 0-based index of the argument.
     * @return The argument's class, or {@code Object.class} if the index is out of bounds.
     */
    public Class<?> getArgumentClass(int index) {
        if (index < 0 || index >= typeArguments.length) {
            SLogger.LOGGER.debug("Requested type at index " + index + " is out of bounds. Returning Object.class");
            return Object.class;
        }
        return rawTypeOf(typeArguments[index]);
    }

    // -------------------------------------------------------------------------
    // Static Utilities
    // -------------------------------------------------------------------------

    /**
     * Resolves a {@link Type} into its corresponding {@link Class}.
     *
     * @param type The type to resolve.
     * @return The resolved class.
     * @throws IllegalArgumentException If the type name is not found or is unresolvable.
     */
    public static Class<?> resolveClass(Type type) {
        SLogger.LOGGER.debug("Resolving class for " + type);
        if (type instanceof Class<?> cls) {
            SLogger.LOGGER.debug("   The type is already a class.");
            return cls;
        }
        if (type instanceof ParameterizedType pt) {
            SLogger.LOGGER.debug("   The type is parameterized, retrieving the raw type.");
            return (Class<?>) pt.getRawType();
        }

        // Fallback: resolution via name (useful for TypeVariables or generic descriptors)
        String name = Utility.removeGeneric(type.getTypeName());
        try {
            SLogger.LOGGER.debug("   Trying to retrieve from class name: " + name);
            return Class.forName(name, true, ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to resolve type: " + type.getTypeName(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Private Helpers
    // -------------------------------------------------------------------------

    /**
     * Extracts generic type arguments from a field, handling arrays and parameterized types.
     */
    private static Type[] resolveTypeArguments(Field field) {
        SLogger.LOGGER.debug("Resolving arguments of " + field);

        // Handle Arrays: the only "argument" is the component type
        Class<?> component = field.getType().getComponentType();
        if (component != null) {
            SLogger.LOGGER.debug("   Is an array, arguments: " + component);
            return new Type[]{component};
        }

        // Handle Generic Types (e.g., List<String>, Map<K, V>)
        Type generic = field.getGenericType();
        if (generic instanceof ParameterizedType pt) {
            Type[] arguments = pt.getActualTypeArguments();
            SLogger.LOGGER.debug("   Has generics, arguments: " + Arrays.toString(arguments));
            return arguments;
        }

        // Simple types have no type arguments
        SLogger.LOGGER.debug("   Simple object, no arguments.");
        return new Type[0];
    }

    /**
     * Maps a Type to a Class, defaulting to Object if the mapping is complex.
     */
    private static Class<?> rawTypeOf(Type type) {
        return switch (type) {
            case Class<?> cls -> cls;
            case ParameterizedType pt -> (Class<?>) pt.getRawType();
            default -> Object.class;
        };
    }

    // -------------------------------------------------------------------------
    // Utility methods for Serializer
    // -------------------------------------------------------------------------
    public boolean isKeyObjectMap() {
        return getTypeClass().isAssignableFrom(Map.class) && getArgumentClass(0).isAssignableFrom(String.class);
    }


    // -------------------------------------------------------------------------
    // Overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        String name = typeClass.getName().replace("class ", "");

        if (typeClass.isArray()) {
            return "Array of " + typeClass.getComponentType().getName();
        } else if (typeClass.isAssignableFrom(Collection.class)) {
            return "Collection of " + typeArguments[0].getTypeName();
        } else if (typeClass.isAssignableFrom(Map.class)) {
            return "Map of " + typeArguments[0].getTypeName() + " - " + typeArguments[1].getTypeName();
        }

        return name;
    }
}