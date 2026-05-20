package me.tr.trserializer.types;

import me.tr.trserializer.utility.SLogger;
import me.tr.trserializer.utility.Utility;
import me.tr.trserializer.utility.Wrappers;

import java.lang.reflect.*;
import java.util.Arrays;
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
    public static final GenericType<String> STRING = new GenericType<>(String.class);
    public static final GenericType<Number> NUMBER = new GenericType<>(Number.class);
    public static final GenericType<Boolean> BOOLEAN = new GenericType<>(Boolean.class);
    public static final GenericType<Map<String, Object>> SERIALIZED_OBJECT = new GenericType<>(Map.class, String.class, Object.class);
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
            SLogger.LOGGER.debug("Some arguments are provided to " + this + ". Using them...");
            this.typeArguments = typeArguments.clone();
        } else {
            SLogger.LOGGER.debug("No arguments are provided to " + this + ". Trying to auto-detect them...");
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
        if (type == null) {
            SLogger.LOGGER.debug("The provided type is null. Cannot resolve class for it.");
            return Object.class;
        }
        SLogger.LOGGER.debug("Resolving class for " + type.getClass());
        switch (type) {
            case Class<?> cls -> {
                SLogger.LOGGER.debug("  " + type.getClass() + " is a class already.");
                return cls;
            }
            case ParameterizedType pt -> {
                SLogger.LOGGER.debug("  " + type.getClass() + " is a parameterized type.");
                return resolveClass(pt.getRawType());
            }
            case GenericArrayType gat -> {
                SLogger.LOGGER.debug("  " + type.getClass() + " is a generic array type.");
                Class<?> componentClass = resolveClass(gat.getGenericComponentType());
                return componentClass.arrayType();
            }
            case TypeVariable<?> tv -> {
                SLogger.LOGGER.debug("  " + type.getClass() + " is a type variable.");
                if (tv.getBounds().length > 0) {
                    return resolveClass(tv.getBounds()[0]);
                }
                return Object.class;
            }
            case WildcardType wt -> {
                SLogger.LOGGER.debug("  " + type.getClass() + " is a wildcard type.");
                if (wt.getUpperBounds().length > 0) {
                    return resolveClass(wt.getUpperBounds()[0]);
                }
                return Object.class;
            }
            default -> {
                SLogger.LOGGER.debug("  Trying to resolve type " + type.getClass());
                String name = Utility.removeGeneric(type.getTypeName());
                SLogger.LOGGER.debug("  Class name: " + name);
                try {
                    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                    ClassLoader loaderToUse = (contextClassLoader != null) ? contextClassLoader : ClassLoader.getSystemClassLoader();

                    return Class.forName(name, true, loaderToUse);
                } catch (ClassNotFoundException e) {
                    SLogger.LOGGER.debug("  Unable to resolve class name for: " + type.getTypeName() + ". Falling back to Object.class");
                    return Object.class;
                }
            }
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
    public static Type[] extractTypeArguments(Type type) {
        if (type instanceof ParameterizedType pt) {
            SLogger.LOGGER.debug("  Resolving arguments of parameterized type: " + pt.getRawType().getTypeName());
            return Arrays.stream(pt.getActualTypeArguments())
                    .flatMap(t -> Arrays.stream(extractTypeArguments(t)))
                    .toArray(Type[]::new);
        }

        if (type instanceof GenericArrayType gat) {
            SLogger.LOGGER.debug("  Resolving arguments of generic array type: " + gat.getTypeName());
            return extractTypeArguments(gat.getGenericComponentType());
        }

        if (type instanceof Class<?> cls) {
            if (cls.isArray()) {
                SLogger.LOGGER.debug("  Resolving arguments of array: " + cls.getName());
                return extractTypeArguments(cls.getComponentType());
            }
        }

        SLogger.LOGGER.debug("  No arguments found for " + type.getTypeName());
        return new Type[]{type};
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
                && Wrappers.isAssignable(String.class, getFirstArgumentClass());
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