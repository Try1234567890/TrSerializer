package me.tr.trserializer.options;

import java.util.*;

/**
 * Represent an option that contains a {@link List}.
 * <p>This implements {@link List} so can be provided as a {@link List} too.
 */
public class ListOption<V> extends Option<List<V>> implements List<V> {


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
        return new HashSet<>(get()).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        return get().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends V> c) {
        return get().addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return get().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return get().retainAll(c);
    }

    @Override
    public void clear() {
        get().clear();
    }

    @Override
    public V get(int index) {
        return get().get(index);
    }

    public Optional<V> retrieve(int index) {
        return Optional.ofNullable(get().get(index));
    }

    @Override
    public V set(int index, V element) {
        return get().get(index);
    }

    @Override
    public void add(int index, V element) {
        get().add(index, element);
    }

    @Override
    public V remove(int index) {
        return get().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return get().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return get().lastIndexOf(o);
    }

    @Override
    public ListIterator<V> listIterator() {
        return get().listIterator();
    }

    @Override
    public ListIterator<V> listIterator(int index) {
        return get().listIterator(index);
    }

    @Override
    public List<V> subList(int fromIndex, int toIndex) {
        return get().subList(fromIndex, toIndex);
    }
}
