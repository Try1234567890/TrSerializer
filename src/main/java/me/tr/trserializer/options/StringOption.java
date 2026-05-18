package me.tr.trserializer.options;

/**
 * Represent an option that contains a {@link String}
 */
public class StringOption extends Option<String> {

    /**
     * @return {@code true} if the string is not null and not empty, otherwise {@code false}.
     */
    public boolean hasStringValue() {
        return super.hasValue() && !get().isEmpty();
    }
}
