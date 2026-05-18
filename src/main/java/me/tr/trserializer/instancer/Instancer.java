package me.tr.trserializer.instancer;

import me.tr.trserializer.exceptions.InstancerError;
import me.tr.trserializer.types.GenericType;

import java.util.HashMap;
import java.util.Map;

/**
 * An instancer is a system that provide
 * a new instance of a provided class.
 */
public interface Instancer {

    /**
     * Instance a new class.
     *
     * @param cls    The class to instance.
     * @param params The params to provide to the constructor (or method).
     * @param <T>    The class and new instance type.
     * @return A new instance of the provided class.
     * @throws InstancerError if an error occurs while instancing the class.
     */
    <T> T instance(Class<T> cls, Map<String, Object> params) throws InstancerError;

    /**
     * @return the instancer options
     */
    InstancerOptions getOptions();

    /**
     * Instance a new class.
     *
     * @param cls The class to instance.
     * @param <T> The class and new instance type.
     * @return A new instance of the provided class.
     * @throws InstancerError if an error occurs while instancing the class.
     */
    default <T> T instance(Class<T> cls) {
        return instance(cls, new HashMap<>());
    }

    /**
     * Instance a new class.
     *
     * @param type The class to instance.
     * @param <T>  The class and new instance type.
     * @return A new instance of the provided class.
     * @throws InstancerError if an error occurs while instancing the class.
     */
    default <T> T instance(GenericType<T> type) {
        return instance(type.getTypeClass());
    }

    /**
     * Instance a new class.
     *
     * @param type   The class to instance.
     * @param params The params to provide to the constructor (or method).
     * @param <T>    The class and new instance type.
     * @return A new instance of the provided class.
     * @throws InstancerError if an error occurs while instancing the class.
     */
    default <T> T instance(GenericType<T> type, Map<String, Object> params) {
        return instance(type.getTypeClass(), params);
    }

    /**
     * Instance a new class as singleton.
     *
     * @param cls The class to instance.
     * @param <T> The class and new instance type.
     * @return A new instance of the provided class.
     * @throws InstancerError if an error occurs while instancing the class.
     */
    default <T> T instanceAsSingleton(Class<T> cls) {
        T instance = instance(cls, new HashMap<>());
        SingletonInstances.newSingleton(cls, instance);
        return instance;
    }

}
