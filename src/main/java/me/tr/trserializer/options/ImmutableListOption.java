package me.tr.trserializer.options;

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
        return true;
    }

    @Override
    public void add(int index, V element) {

    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends V> c) {
        return false;
    }

    @Override
    public void addFirst(V v) {

    }

    @Override
    public void addLast(V v) {

    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public V remove(int index) {
        return null;
    }

    @Override
    public boolean removeIf(Predicate<? super V> filter) {
        return false;
    }

    @Override
    public V removeFirst() {
        return null;
    }

    @Override
    public V removeLast() {
        return null;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }
}
