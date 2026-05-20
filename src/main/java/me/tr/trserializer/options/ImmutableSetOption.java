package me.tr.trserializer.options;

import me.tr.trserializer.utility.SLogger;

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
        SLogger.LOGGER.warn("Cannot modify immutable set.");
        return false;
    }

    @Override
    public boolean remove(Object o) {
        SLogger.LOGGER.warn("Cannot modify immutable set.");
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        SLogger.LOGGER.warn("Cannot modify immutable set.");
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        SLogger.LOGGER.warn("Cannot modify immutable set.");
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        SLogger.LOGGER.warn("Cannot modify immutable set.");
        return false;
    }

    @Override
    public boolean removeIf(Predicate<? super V> filter) {
        SLogger.LOGGER.warn("Cannot modify immutable set.");
        return false;
    }

    @Override
    public void clear() {
        SLogger.LOGGER.warn("Cannot modify immutable set.");

    }
}
