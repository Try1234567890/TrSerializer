package me.tr.trserializer.instancer;

import me.tr.trserializer.registries.Registry;
import me.tr.trserializer.utility.SLogger;
import me.tr.trserializer.utility.Utility;

/**
 * This class is a registry that contains all the singleton marked
 * instances successfully instanced by an {@link Instancer}.
 * <p>
 * If an instance is marked as singleton, the instantiation process
 * will be executed once and the instance will be saved.
 * Other times will be used the saved instance.
 */
public class SingletonInstances extends Registry<Class<?>, Object> {
    private static final SingletonInstances INSTANCE = new SingletonInstances();

    private SingletonInstances() {
    }


    public static SingletonInstances getInstance() {
        return INSTANCE;
    }

    /**
     * Checks if any instance is saved in the singletons instances.
     *
     * @param cls The class to check.
     * @return {@code true} if is present in singleton instances, otherwise {@code false}.
     */
    public static boolean isSingleton(Class<?> cls) {
        return getInstance().containsKey(cls);
    }

    /**
     * Register the {@code instance} as singleton instance for the {@code class}
     *
     * @param cls      The class.
     * @param instance The instance.
     * @param <T>      The class and instance type.
     */
    public static <T> void newSingleton(Class<T> cls, T instance) {
        if (isSingleton(cls)) {
            SLogger.LOGGER.info("The class " + Utility.getClassName(cls) + " is already singleton.");
            return;
        }
        getInstance().register(cls, instance);
    }


    /**
     * Retrieve the {@code instance} from the singleton instance of the {@code class}
     *
     * @param cls      The class.
     * @param <T>      The class and instance type.
     * @return The class {@code instance} if found, otherwise {@code null}.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> cls) {
        if (!isSingleton(cls)) {
            SLogger.LOGGER.error("The class " + Utility.getClassName(cls) + " is not singleton.");
            return null;
        }
        return (T) getInstance().get(cls);
    }
}
