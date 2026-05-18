package me.tr.trserializer.translator;

import me.tr.trserializer.instancer.TranslatorInstancer;
import me.tr.trserializer.types.GenericType;

import java.util.UUID;

/**
 * This interface define the basics of a task
 * for a translator.
 * All translator task should implement it.
 */
public interface TranslatorTask {

    /**
     * @return the identifier of this task
     */
    UUID getID();

    /**
     * @return the translator that creates this task.
     */
    Translator getTranslator();

    /**
     * @return The object to work on.
     */
    Object getObject();

    /**
     * @return The expected final object type.
     */
    GenericType<?> getGenericType();

    /**
     * @return The result of this class.
     */
    Result getResult();

    /**
     * @return a short-cut for {@link Translator#getInstancer()}
     */
    default TranslatorInstancer getInstancer() {
        return getTranslator().getInstancer();
    }

    /**
     * Checks if this is a field task.
     *
     * @return {@code true} if it is, otherwise {@code false}.
     */
    default boolean isFieldTask() {
        return this instanceof FieldTask;
    }

    /**
     * Convert this task to a {@link FieldTask}.
     * <p>
     * This method should be always be preceded by {@link #isFieldTask()}.
     *
     * @return the {@link FieldTask} version of this task.
     * @throws IllegalStateException if this is not a {@link FieldTask}.
     */
    default FieldTask asFieldTask() {
        if (!isFieldTask()) throw new IllegalStateException("Is not a field task.");
        return (FieldTask) this;
    }
}
