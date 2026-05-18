package me.tr.trserializer.options;

/**
 * Represent a generic option. This is the
 * default implementation of the option.
 */
public class Option<T> implements IOption<T> {
    private T value;

    public Option() {
    }

    public Option(T value) {
        this.value = value;
    }

    /**
     * Set the value of this option
     *
     * @param newValue The new value.
     * @return {@code true} if ends successfully, otherwise {@code false}.
     */
    @Override
    public boolean set(T newValue) {
        this.value = newValue;
        return true;
    }

    /**
     * @return the value of this option.
     */
    @Override
    public T get() {
        return value;
    }
}
