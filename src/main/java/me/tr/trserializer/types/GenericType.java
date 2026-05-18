package me.tr.trserializer.types;

import me.tr.trserializer.utility.SLogger;
import me.tr.trserializer.utility.Utility;
import me.tr.trserializer.utility.Wrappers;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;

/**
 * Represents a generic type at runtime, encapsulating both the raw class
 * and its associated type arguments or component types.
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
     * If no type arguments are provided, it attempts to extract them automatically from the raw type.
     *
     * @param rawType       The base type (e.g., {@code List.class} or {@code List<String>}).
     * @param typeArguments The generic arguments (e.g., {@code String.class}).
     */
    @SuppressWarnings("unchecked")
    public GenericType(Type rawType, Type... typeArguments) {
        this.typeClass = (Class<T>) resolveClass(rawType);

        // If explicit type arguments are provided, use them; otherwise, attempt auto-extraction
        if (typeArguments != null && typeArguments.length > 0) {
            this.typeArguments = typeArguments.clone();
        } else {
            this.typeArguments = extractTypeArguments(rawType);
        }
    }

    /**
     * Infers the raw type and generic arguments from a specific {@link Field}.
     *
     * @param field The field to inspect.
     */
    @SuppressWarnings("unchecked")
    public GenericType(Field field) {
        // Use the field's generic type to avoid losing nested type arguments (e.g., generic arrays)
        this.typeClass = (Class<T>) resolveClass(field.getGenericType());
        this.typeArguments = resolveTypeArguments(field);
    }

    // -------------------------------------------------------------------------
    // Factory Methods
    // -------------------------------------------------------------------------

    public static <T> GenericType<T> of(Class<T> clazz, Type... typeArguments) {
        return new GenericType<>(clazz, typeArguments);
    }

    public static <T> GenericType<T> of(Class<T> clazz) {
        return new GenericType<>(clazz);
    }

    public static <T> GenericType<T> of(Field field) {
        return new GenericType<>(field);
    }

    // -------------------------------------------------------------------------
    // ParameterizedType Implementation
    // -------------------------------------------------------------------------

    @Override
    public Type getRawType() {
        return typeClass;
    }

    @Override
    public Type getOwnerType() {
        // Return the enclosing class if it's an inner/member class; otherwise null
        return typeClass.isMemberClass() ? typeClass.getEnclosingClass() : null;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments.clone();
    }

    // -------------------------------------------------------------------------
    // Additional API
    // -------------------------------------------------------------------------

    public Class<T> getTypeClass() {
        return typeClass;
    }

    public Class<?> getFirstArgumentClass() {
        return getArgumentClass(0);
    }

    public Class<?> getArgumentClass(int index) {
        if (index < 0 || index >= typeArguments.length) {
            SLogger.LOGGER.debug("Requested type at index " + index + " is out of bounds. Returning Object.class");
            return Object.class;
        }
        return resolveClass(typeArguments[index]);
    }

    // -------------------------------------------------------------------------
    // Static Utilities (The Core Engine)
    // -------------------------------------------------------------------------

    /**
     * Resolves ANY {@link Type} into its closest corresponding {@link Class}.
     * Recursively unwraps Classes, Parameterized Types, Generic Arrays, Type Variables, and Wildcards.
     */
    public static Class<?> resolveClass(Type type) {
        if (type == null) return Object.class;

        // 1. Direct Class instance
        if (type instanceof Class<?> cls) {
            return cls;
        }
        // 2. Parameterized Type (e.g., List<String> -> List.class)
        if (type instanceof ParameterizedType pt) {
            return resolveClass(pt.getRawType());
        }
        // 3. Generic Array Type (e.g., T[] or List<String>[])
        if (type instanceof GenericArrayType gat) {
            // Dynamically instantiate a mirror array class using the resolved component type
            Class<?> componentClass = resolveClass(gat.getGenericComponentType());
            return Array.newInstance(componentClass, 0).getClass();
        }
        // 4. Type Variable (e.g., T extends Number -> Number.class)
        if (type instanceof TypeVariable<?> tv) {
            if (tv.getBounds().length > 0) {
                return resolveClass(tv.getBounds()[0]);
            }
            return Object.class;
        }
        // 5. Wildcard Type (e.g., ? extends String -> String.class)
        if (type instanceof WildcardType wt) {
            if (wt.getUpperBounds().length > 0) {
                return resolveClass(wt.getUpperBounds()[0]);
            }
            return Object.class;
        }

        // Extreme fallback: String-based resolution via standard ClassLoader
        String name = Utility.removeGeneric(type.getTypeName());
        try {
            return Class.forName(name, true, ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException e) {
            SLogger.LOGGER.debug("Unable to resolve class name for: " + type.getTypeName() + ". Falling back to Object.class");
            return Object.class;
        }
    }

    /**
     * Extract type arguments aggressively from a Field by looking up its Generic Type structure.
     */
    private static Type[] resolveTypeArguments(Field field) {
        SLogger.LOGGER.debug("Resolving arguments of field: " + field.getName());
        return extractTypeArguments(field.getGenericType());
    }

    /**
     * Internal recursive logic to dive into generic hierarchies and retrieve type definitions.
     */
    private static Type[] extractTypeArguments(Type type) {
        // Handle standard generics (e.g., Collection<String> -> [String.class])
        if (type instanceof ParameterizedType pt) {
            return pt.getActualTypeArguments();
        }

        // Handle Generic Arrays (e.g., List<String>[])
        if (type instanceof GenericArrayType gat) {
            Type componentType = gat.getGenericComponentType();
            // If the component is parameterized, extract its arguments (e.g., String from List<String>)
            if (componentType instanceof ParameterizedType ptComponent) {
                return ptComponent.getActualTypeArguments();
            }
            return new Type[]{componentType};
        }

        // Handle standard Class references and standard arrays
        if (type instanceof Class<?> cls) {
            if (cls.isArray()) {
                // For regular arrays (e.g., String[]), the component type acts as the single type argument
                return new Type[]{cls.getComponentType()};
            }
        }

        // Return empty array for raw types and unhandled structures
        return new Type[0];
    }

    // -------------------------------------------------------------------------
    // Utility methods for Serializer
    // -------------------------------------------------------------------------

    /**
     * Checks if the type represents a Map implementation where keys are Strings.
     */
    public boolean isKeyObjectMap() {
        return Map.class.isAssignableFrom(typeClass)
                && typeArguments.length > 0
                && String.class.isAssignableFrom(getArgumentClass(0));
    }

    public boolean is(Class<?> clazz) {
        return Wrappers.isAssignable(typeClass, clazz);
    }

    public boolean isEnum() {
        return typeClass.isEnum();
    }

    public boolean isRecord() {
        return typeClass.isRecord();
    }

    // -------------------------------------------------------------------------
    // Overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        String name = typeClass.getName();

        if (typeClass.isArray()) {
            return "Array of " + typeClass.getComponentType().getName();
        }

        if (Collection.class.isAssignableFrom(typeClass) && typeArguments.length > 0) {
            return "Collection of " + typeArguments[0].getTypeName();
        }

        if (Map.class.isAssignableFrom(typeClass) && typeArguments.length >= 2) {
            return "Map of " + typeArguments[0].getTypeName() + " - " + typeArguments[1].getTypeName();
        }

        return name;
    }
}