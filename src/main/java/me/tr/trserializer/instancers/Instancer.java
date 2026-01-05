package me.tr.trserializer.instancers;


public interface Instancer {

    /**
     * Instance the class.
     *
     * @param clazz The class to instance.
     * @return The instance of the provided class if the process ends successfully, otherwise {@code null}.
     */
    Object instance(Class<?> clazz);

    /**
     * Checks if the process failed.
     *
     * @return {@code true} if the process failed, otherwise {@code false}.
     */
    boolean isFailed();

    /**
     * Get the reason of the failure.
     *
     * @return The reason of the failure.
     */
    Throwable getReason();

    /**
     * Clean the instancer from
     * previous executions.
     */
    void reset();
}
