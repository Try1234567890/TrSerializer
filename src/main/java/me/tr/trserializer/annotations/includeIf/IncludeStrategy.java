package me.tr.trserializer.annotations.includeIf;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Optional;

public enum IncludeStrategy {

    NOT_NULL {
        @Override
        public boolean isValid(Object obj) {
            return obj != null;
        }
    },

    NOT_EMPTY {
        @Override
        public boolean isValid(Object obj) {
            switch (obj) {
                case null -> {
                    return false;
                }
                case Iterable<?> it -> {
                    return it.iterator().hasNext();
                }
                case Map<?, ?> map -> {
                    return !map.isEmpty();
                }
                case String str -> {
                    return !str.isEmpty();
                }
                case Optional<?> opt -> {
                    return opt.isPresent();
                }
                case Reference<?> ref -> {
                    return ref.get() != null;
                }
                default -> {
                }
            }

            if (obj.getClass().isArray())
                return Array.getLength(obj) != 0;

            return true;
        }
    },

    NOT_EMPTY_RECURSIVE {
        @Override
        public boolean isValid(Object obj) {
            if (obj instanceof Optional<?> opt)
                return opt.isPresent() && isValid(opt.get());

            if (obj instanceof Reference<?> ref) {
                return isValid(ref.get());
            }

            return NOT_EMPTY.isValid(obj);
        }
    };


    public abstract boolean isValid(Object obj);

}
