package me.tr.trserializer.registries;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionRegistry<E> {
    // CopyOnWriteArrayList is thread-safe and preserves insertion order.
    // It is highly optimized for frequent reads and infrequent writes.
    protected final List<E> internalCollection = new CopyOnWriteArrayList<>();

    /**
     * Registers a new element into the registry.
     *
     * @return true if the element was successfully added.
     */
    public boolean register(E element) {
        if (element == null) {
            throw new IllegalArgumentException("Registered element cannot be null");
        }
        return internalCollection.add(element);
    }

    /**
     * Registers all elements from the provided collection into this registry.
     */
    public void registerAll(Collection<? extends E> elements) {
        if (elements == null) {
            throw new IllegalArgumentException("Elements collection cannot be null");
        }
        elements.forEach(this::register);
    }

    /**
     * Registers an element only if it is not already present in the registry.
     *
     * @return true if the element was added.
     */
    public boolean registerIfAbsent(E element) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        if (!internalCollection.contains(element)) {
            return internalCollection.add(element);
        }
        return false;
    }

    /**
     * Unregisters and removes an element from the registry.
     *
     * @return true if the element was found and removed.
     */
    public boolean unregister(E element) {
        return internalCollection.remove(element);
    }

    /**
     * Removes all elements that match the given predicate.
     * Highly useful for dynamic conditional unregistration.
     */
    public void unregisterIf(Predicate<? super E> filter) {
        internalCollection.removeIf(filter);
    }

    /**
     * Finds the first element in the registry that matches a specific condition.
     * This is the core lookup method for a collection-based registry.
     * (e.g., registry.find(handler -> handler.canHandle(objectClass)))
     */
    public Optional<E> find(Predicate<? super E> predicate) {
        return stream().filter(predicate).findFirst();
    }

    /**
     * Finds all elements that match a specific predicate and returns them as a List.
     * Checks the parent registry chain as well.
     */
    public List<E> findAll(Predicate<? super E> predicate) {
        return stream().filter(predicate).toList();
    }

    /**
     * Returns the first element matching the predicate, or throws a custom exception
     * if no matching element is found anywhere in the chain.
     */
    public <X extends Throwable> E getOrThrow(Predicate<? super E> predicate, Supplier<? extends X> exceptionSupplier) throws X {
        return find(predicate).orElseThrow(exceptionSupplier);
    }

    /**
     * Filters all elements that match a specific sub-type and returns them as an unmodifiable set.
     * Useful if E is a generic interface (e.g., Addon) and you need specific implementations.
     */
    @SuppressWarnings("unchecked")
    public <T extends E> Set<T> getElementsOfType(Class<T> type) {
        return stream()
                .filter(type::isInstance)
                .map(element -> (T) element)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Checks if a specific element is explicitly present in this local registry instance.
     */
    public boolean contains(E element) {
        return internalCollection.contains(element);
    }

    /**
     * Checks if any element in the registry matches the given predicate.
     */
    public boolean anyMatch(Predicate<? super E> predicate) {
        return stream().anyMatch(predicate);
    }

    /**
     * Completely clears the local registry.
     */
    public void clear() {
        internalCollection.clear();
    }

    /**
     * Returns the number of elements currently registered locally.
     */
    public int size() {
        return internalCollection.size();
    }

    /**
     * Checks if the local registry is empty.
     */
    public boolean isEmpty() {
        return internalCollection.isEmpty();
    }

    /**
     * Performs the given action for each element in this local registry.
     */
    public void forEach(Consumer<? super E> action) {
        internalCollection.forEach(action);
    }

    // --- Exportation & Utility Streams ---

    /**
     * Returns an unmodifiable Collection view of the elements contained in this local registry.
     */
    public Collection<E> asCollection() {
        return Collections.unmodifiableCollection(internalCollection);
    }

    /**
     * Provides a Stream of the locally registered elements.
     */
    public Stream<E> stream() {
        return internalCollection.stream();
    }
}