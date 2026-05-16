package me.tr.trserializer.annotations.filter.includeIf;

import java.lang.ref.Reference;
import java.util.Map;

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
            return switch (obj) {
                case Iterable<?> it -> it.iterator().hasNext();
                case Map<?, ?> map -> !map.isEmpty();
                case String str -> !str.isEmpty();
                case Object[] arr -> arr.length != 0;
                case null, default -> false;
            };
        }
    },

    NOT_EMPTY_RECURSIVE {
        @Override
        public boolean isValid(Object obj) {
            if (obj instanceof Reference<?> ref) {
                return isValid(ref.get());
            }

            return NOT_EMPTY.isValid(obj);
        }
    };


    public abstract boolean isValid(Object obj);

}
