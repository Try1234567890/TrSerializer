package me.tr.trserializer.translator;

import me.tr.trserializer.utility.SLogger;

import java.util.function.Consumer;

/**
 * This class represent the final result of a {@link TranslatorTask}.
 * This class contains a {@link Consumer} to support special implementation
 * where the final result is not guarantee too (like the Iterative version).
 * <p>
 * This class contains an Object {@link #result} field that is filled with the
 * {@code Object result} provided to the {@link Consumer} when the {@link Consumer#accept(Object)}
 * method is called.
 * If the {@code result field} is {@code not null} the {@link Consumer#accept(Object)} cannot be executed.
 */
public class Result implements Consumer<Object> {
    private final TranslatorTask task;
    private final Consumer<Object> consumer;
    private Object result;

    public Result(TranslatorTask task, Consumer<Object> consumer) {
        this.task = task;
        this.consumer = consumer;
    }

    @Override
    public final void accept(Object initialResult) {
        if (hasResult()) {
            SLogger.LOGGER.warn(this + " has already been set! Cannot accept another result.");
            return;
        }
        Object processedResult = initialResult;

        // TODO: Add the addons & filter execution
        //       when respective managers are finished

        this.result = processedResult;
        getConsumer().accept(processedResult);
    }

    public final Object acceptAndReturn(Object initialResult) {
        if (hasResult()) {
            SLogger.LOGGER.warn(this + " has already been set! Returning current result.");
            return result;
        }
        accept(initialResult);
        return result;
    }

    /**
     * @return the task that owns this result.
     */
    public TranslatorTask getTask() {
        return task;
    }

    /**
     * @return the consumer of this result
     */
    public Consumer<Object> getConsumer() {
        return consumer;
    }

    /**
     * @return the object provided to the consumer, or {@code null} if {@link #accept(Object)} has not already called.
     * @see #hasResult()
     */
    public Object getResult() {
        return result;
    }

    /**
     * Checks if the result has been filled with some value.
     *
     * @return {@code true} if has been filled, otherwise {@code false}.
     * @see #getResult()
     */
    public boolean hasResult() {
        return result != null;
    }

    @Override
    public String toString() {
        return "Result[Task=" + task + ", Result=" + result + ']';
    }
}
