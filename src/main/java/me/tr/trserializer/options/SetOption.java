package me.tr.trserializer.options;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Represent an option that contains a {@link Set}.
 * <p>This implements {@link Set} so can be provided as a {@link Set} too.
 */
public class SetOption<V> extends Option<Set<V>> implements Set<V> {

    @Override
    public int size() {
        return get().size();
    }

    @Override
    public boolean isEmpty() {
        return get().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return get().contains(o);
    }

    @Override
    public Iterator<V> iterator() {
        return get().iterator();
    }

    @Override
    public Object[] toArray() {
        return get().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return get().toArray(a);
    }

    @Override
    public boolean add(V v) {
        return get().add(v);
    }

    @Override
    public boolean remove(Object o) {
        return get().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return get().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        return get().addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return get().retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return get().removeAll(c);
    }

    @Override
    public void clear() {
        get().clear();
    }

    /**
     * @return {@code true} if value is not null and not empty, otherwise {@code false}.
     */
    public boolean hasSetValue() {
        return super.hasValue() && !get().isEmpty();
    }
}
