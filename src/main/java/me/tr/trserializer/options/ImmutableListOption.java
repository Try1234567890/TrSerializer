package me.tr.trserializer.options;

import me.tr.trserializer.utility.SLogger;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represent an option that contains a {@link List}.
 * <p>This implements {@link List} so can be provided as a {@link List} too.
 * <p>
 * This implementation is immutable, so any remove, add or retain operation
 * will be ignored.
 */
public class ImmutableListOption<V> extends ListOption<V> {

    @Override
    public boolean add(V v) {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
        return true;
    }

    @Override
    public void add(int index, V element) {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends V> c) {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
        return false;
    }

    @Override
    public void addFirst(V v) {
        SLogger.LOGGER.warn("Cannot modify immutable list.");

    }

    @Override
    public void addLast(V v) {
        SLogger.LOGGER.warn("Cannot modify immutable list.");

    }

    @Override
    public boolean remove(Object o) {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
        return false;
    }

    @Override
    public V remove(int index) {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
        return null;
    }

    @Override
    public boolean removeIf(Predicate<? super V> filter) {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
        return false;
    }

    @Override
    public V removeFirst() {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
        return null;
    }

    @Override
    public V removeLast() {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
        return null;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
        return false;
    }

    @Override
    public void clear() {
        SLogger.LOGGER.warn("Cannot modify immutable list.");
    }
}
