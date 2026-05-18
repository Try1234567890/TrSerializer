package me.tr.trserializer.options;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Represent an option that contains a {@link Set}.
 * <p>This implements {@link Set} so can be provided as a {@link Set} too.
 * <p>
 * This implementation is immutable, so any remove, add or retain operation
 * will be ignored.
 */
public class ImmutableSetOption<V> extends SetOption<V> {
    @Override
    public boolean add(V v) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeIf(Predicate<? super V> filter) {
        return false;
    }

    @Override
    public void clear() {

    }
}
