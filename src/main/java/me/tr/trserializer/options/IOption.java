package me.tr.trserializer.options;

/**
 * This interface represent the base declaration
 * of any option for any process (like: Instancer)
 * or translator (like: Serializers or Deserializers).
 */
public interface IOption<T> {

    /**
     * Change the current value with the {@code new value}.
     *
     * @param newValue the new value.
     * @return {@code true} if the operation ends successfully, otherwise {@code false}.
     */
    boolean set(T newValue);

    /**
     * Retrieve the current value of this option.
     *
     * @return the current value of this option.
     */
    T get();

    /**
     * Checks if the current value has a value. (or if the value != null)
     *
     * @return {@code true} if the value is not null, otherwise {@code false}.
     */
    default boolean hasValue() {
        return get() != null;
    }
}
