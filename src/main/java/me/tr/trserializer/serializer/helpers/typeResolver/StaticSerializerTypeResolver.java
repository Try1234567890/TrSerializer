package me.tr.trserializer.serializer.helpers.typeResolver;

import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.*;

public final class StaticObjectSerializerTypeResolver implements SerializerTypeResolver {
    public static final StaticObjectSerializerTypeResolver INSTANCE = new StaticObjectSerializerTypeResolver();

    private StaticObjectSerializerTypeResolver() {
    }

    public static GenericType<?> resolve(Object o) {
        return INSTANCE.getType(o);
    }

    @Override
    public GenericType<?> getType(Object obj) {
        if (obj == null) return GenericType.of(Object.class);

        Class<?> cls = obj.getClass();

        if ((cls.isPrimitive() || cls.isAssignableFrom(CharSequence.class)) || Utility.isWrapper(cls))
            return GenericType.of(cls);

        return switch (obj) {
            case Object[] arr -> getType(arr);
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

    private GenericType<?> getType(Map<?, ?> map) {
        if (map.isEmpty()) return GenericType.of(Map.class, Object.class, Object.class);
        Object firstK = Utility.getFirstKeyNonNull(map);
        Object firstV = Utility.getFirstValueNonNull(map);
        Class<?> firstKCls = firstK == null ? Object.class : firstK.getClass();
        Class<?> firstVCls = firstV == null ? Object.class : firstV.getClass();

        return GenericType.of(Map.class, firstKCls, firstVCls);
    }

    private GenericType<?> getType(Collection<?> coll) {
        if (coll.isEmpty()) return GenericType.of(Object.class);
        Object firstNonNull = Utility.getFirstValueNonNull(coll);

        return getType(firstNonNull);
    }

    private GenericType<?> getType(Object[] arr) {
        if (arr.length == 0) return GenericType.of(Object.class);
        Object firstNonNull = Utility.getFirstValueNonNull(arr);

        return getType(firstNonNull);
    }

    private GenericType<?> getType(Optional<?> opt) {
        if (opt.isPresent()) return getType(opt.get());
        else return GenericType.of(Object.class);
    }

    private GenericType<?> getType(Reference<?> ref) {
        Object value = ref.get();
        if (value == null) return GenericType.of(Object.class);
        else return getType(value);
    }

    private GenericType<?> getType(AtomicReference<?> atomic) {
        Object value = atomic.get();
        if (value == null) return GenericType.of(Object.class);
        else return getType(value);
    }

    private GenericType<?> getType(AtomicReferenceArray<?> atomic) {
        Object value = atomic.get(0);
        if (value == null) return GenericType.of(Object.class);
        return getType(value);
    }
}













