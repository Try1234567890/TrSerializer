package me.tr.trserializer.serializer.helpers.savable;

import me.tr.trserializer.utility.Utility;

import java.util.Collection;
import java.util.Map;

public final class StaticSavabilityChecker implements SavabilityChecker {
    public static final StaticSavabilityChecker INSTANCE = new StaticSavabilityChecker();

    private StaticSavabilityChecker() {
    }

    public static boolean check(Object object) {
        return INSTANCE.isSavable(object);
    }

    @Override
    public boolean isSavable(Object obj) {
        if (obj == null) {
            // TODO: Add option to allow null value.
            return true;
        }

        Class<?> cls = obj.getClass();
        return switch (obj) {
            case Object[] arr -> isSavable(arr);
            case Collection<?> coll -> isSavable(coll);
            case Map<?, ?> map -> isSavable(map);
            default -> (cls.isPrimitive() || cls.isAssignableFrom(CharSequence.class)) || Utility.isWrapper(cls);
        };
    }

    private boolean isSavable(Object[] arr) {
        if (arr.length == 0) return true;
        Object firstNonNull = Utility.getFirstValueNonNull(arr);

        return isSavable(firstNonNull);
    }

    private boolean isSavable(Collection<?> coll) {
        if (coll.isEmpty()) return true;
        Object firstNonNull = Utility.getFirstValueNonNull(coll);

        return isSavable(firstNonNull);
    }

    private boolean isSavable(Map<?, ?> map) {
        if (map.isEmpty()) return true;
        Object firstNonNull = Utility.getFirstValueNonNull(map);

        return isSavable(firstNonNull);
    }
}
