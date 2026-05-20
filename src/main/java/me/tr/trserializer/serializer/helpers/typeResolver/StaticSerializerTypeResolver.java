package me.tr.trserializer.serializer.helpers.typeResolver;

import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.SLogger;
import me.tr.trserializer.utility.Utility;
import me.tr.trserializer.utility.Wrappers;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.*;

/**
 * A serializer type resolver is a system that automatically
 * resolve the output object of a serializing process.
 * <p>
 * This implementation follow the singleton pattern and provide
 * an output type based only on the object that the serializer
 * is processing.
 */
public final class StaticSerializerTypeResolver implements SerializerTypeResolver {
    public static final StaticSerializerTypeResolver INSTANCE = new StaticSerializerTypeResolver();

    private StaticSerializerTypeResolver() {
    }


    /**
     * Retrieve the output object type in the serialization context.
     *
     * @param obj The object.
     * @return the output object type in the serialization context
     */
    public static GenericType<?> resolve(Object obj) {
        return INSTANCE.getType(obj);
    }

    /**
     * Retrieve the output object type in the serialization context.
     *
     * @param obj The object.
     * @return the output object type in the serialization context
     */
    @Override
    public GenericType<?> getType(Object obj) {
        if (obj == null) {
            SLogger.LOGGER.debug("Cannot retrieve type of a null object for the serializer. Returning Object type.");
            return GenericType.of(Object.class);
        }

        Class<?> cls = obj instanceof Class<?> cl ? cl : obj.getClass();

        if (Wrappers.isPrimitiveOrWrapper(cls)) {
            SLogger.LOGGER.debug("The class " + cls.getName() + " is a wrapper or a primitive. Returning it.");
            return GenericType.of(cls);
        }


        if (cls.isArray()) {
            SLogger.LOGGER.debug("The class " + cls.getName() + " is an array. Building the type...");
            // TODO: A small performance-fix to skip the creation of a new array
            //       if the original array is already an array of
            //       savable objects (String, Primitives and Wrappers).
            // TODO: Check for Class#arrayType() method.

            GenericType<?> componentType = getType(cls.getComponentType());
            Object newArray = Array.newInstance(componentType.getTypeClass(), 1);
            Class<?> newArrayClass = newArray.getClass();

            return GenericType.of(newArrayClass, componentType);
        }

        if (cls.isEnum()) {
            // TODO: Adding a way to serialize it as another type.
            return new GenericType<>(String.class);
        }

        return switch (obj) {
            case Collection<?> coll -> getType(coll);
            case Map<?, ?> map -> getType(map);
            case Optional<?> opt -> getType(opt);
            case Reference<?> ref -> getType(ref);
            case AtomicReference<?> atomic -> getType(atomic);
            case AtomicReferenceArray<?> atomic -> getType(atomic);
            case AtomicInteger o -> GenericType.of(int.class);
            case AtomicIntegerArray o -> GenericType.of(int[].class);
            case AtomicLong o -> GenericType.of(long.class);
            case AtomicLongArray o -> GenericType.of(long[].class);
            case AtomicBoolean o -> GenericType.of(Boolean.class);
            default -> GenericType.of(Map.class, String.class, Object.class);
        };
    }

    /**
     * Retrieve the output type of the {@code map} in the serialization context.
     *
     * @param map The map.
     * @return the output type of the {@code map} in the serialization context
     */
    private GenericType<?> getType(Map<?, ?> map) {
        if (map.isEmpty()) {
            SLogger.LOGGER.debug("Cannot retrieve type of an empty map. Returning Map of Objects.");
            return GenericType.of(Map.class, Object.class, Object.class);
        }
        Object firstK = getType(Utility.getFirstKeyNonNull(map));
        Object firstV = getType(Utility.getFirstValueNonNull(map));

        return GenericType.of(Map.class, firstK.getClass(), firstV.getClass());
    }

    /**
     * Retrieve the output type of the {@code collection} in the serialization context.
     *
     * @param coll The collection.
     * @return the output type of the {@code collection} in the serialization context
     */
    private GenericType<?> getType(Collection<?> coll) {
        if (coll.isEmpty()) {
            SLogger.LOGGER.debug("Cannot retrieve type of an empty collection. Returning Collection of Objects.");
            return GenericType.of(Collection.class, Object.class);
        }
        Object firstNonNull = Utility.getFirstValueNonNull(coll);

        return getType(firstNonNull);
    }

    /**
     * Retrieve the output type of the {@code optional} in the serialization context.
     *
     * @param opt The optional.
     * @return the output type of the {@code optional} in the serialization context
     */
    private GenericType<?> getType(Optional<?> opt) {
        if (opt.isPresent()) return getType(opt.get());
        else {
            SLogger.LOGGER.debug("Cannot retrieve type of an empty optional. Returning Object type.");
            return GenericType.of(Object.class);
        }
    }

    /**
     * Retrieve the output type of the {@code ref} in the serialization context.
     *
     * @param ref The ref.
     * @return the output type of the {@code ref} in the serialization context
     */
    private GenericType<?> getType(Reference<?> ref) {
        Object value = ref.get();
        if (value == null) {
            SLogger.LOGGER.debug("Cannot retrieve type of an empty reference. Returning Object.");
            return GenericType.of(Object.class);
        }
        else return getType(value);
    }

    /**
     * Retrieve the output type of the {@code atomic} in the serialization context.
     *
     * @param atomic The atomic.
     * @return the output type of the {@code atomic} in the serialization context
     */
    private GenericType<?> getType(AtomicReference<?> atomic) {
        Object value = atomic.get();
        if (value == null) {
            SLogger.LOGGER.debug("Cannot retrieve type of an empty atomic. Returning Object.");
            return GenericType.of(Object.class);
        }
        else return getType(value);
    }

    /**
     * Retrieve the output type of the {@code atomic} in the serialization context.
     *
     * @param atomic The atomic.
     * @return the output type of the {@code atomic} in the serialization context
     */
    private GenericType<?> getType(AtomicReferenceArray<?> atomic) {
        Object value = atomic.get(0);
        if (value == null) {
            SLogger.LOGGER.debug("Cannot retrieve type of an empty atomic array. Returning Array of Object.");
            return GenericType.of(Object[].class);
        }
        return getType(value);
    }
}













