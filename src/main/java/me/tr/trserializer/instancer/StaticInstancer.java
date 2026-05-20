package me.tr.trserializer.instancer;

import me.tr.trserializer.annotations.instancer.Initialize;
import me.tr.trserializer.exceptions.InstancerError;
import me.tr.trserializer.instancer.instanceMethods.ConstructorInstancer;
import me.tr.trserializer.instancer.instanceMethods.MethodInstancer;
import me.tr.trserializer.utility.SLogger;
import me.tr.trserializer.utility.Utility;
import me.tr.trserializer.utility.Wrappers;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An instancer is a system that provide a new instance of a provided class.
 * <p>
 * This implementation follow the singleton pattern and doesn't have
 * any external information excluding the class to instantiate e the
 * optional parameters provided.
 */
public class StaticInstancer implements Instancer {
    public static final StaticInstancer INSTANCE = new StaticInstancer();
    private final InstancerOptions options = new InstancerOptions();

    private StaticInstancer() {
    }

    public static StaticInstancer getInstance() {
        return INSTANCE;
    }

    /**
     * Instantiate the {@code class}.
     *
     * @param cls    The class to instantiate.
     * @param params The params to provide to the methods/costructors.
     * @param <T>    The class and instance type.
     * @return A {@code new instance} if no error occurs, otherwise {@link Optional#empty()}
     * @throws InstancerError If an error occurs while instancing the {@code class}.
     * @see #instanceAsInstantiable(Class, Map)
     * @see #instanceWithMethods(Class, Map)
     * @see #instanceWithConstructor(Class, Map)
     * @see #instanceWithEmptyConstructor(Class)
     * @see #instanceWithUnsafe(Class)
     * @see #instance(Class, Map)
     */
    public static <T> T newInstance(Class<T> cls, Map<String, Object> params) throws InstancerError {
        return INSTANCE.instance(cls, params);
    }

    /**
     * Instantiate the {@code class}.
     *
     * @param cls The class to instantiate.
     * @param <T> The class and instance type.
     * @return A {@code new instance} if no error occurs, otherwise {@link Optional#empty()}
     * @throws InstancerError If an error occurs while instancing the {@code class}.
     * @see #instanceAsInstantiable(Class, Map)
     * @see #instanceWithMethods(Class, Map)
     * @see #instanceWithConstructor(Class, Map)
     * @see #instanceWithEmptyConstructor(Class)
     * @see #instanceWithUnsafe(Class)
     * @see #instance(Class, Map)
     */
    public static <T> T newInstance(Class<T> cls) throws InstancerError {
        return INSTANCE.instance(cls);
    }

    /**
     * Instantiate the {@code class}.
     *
     * @param cls    The class to instantiate.
     * @param params The params to provide to the methods/costructors.
     * @param <T>    The class and instance type.
     * @return A {@code new instance} if no error occurs, otherwise {@link Optional#empty()}
     * @throws InstancerError If an error occurs while instancing the {@code class}.
     * @see #instanceAsInstantiable(Class, Map)
     * @see #instanceWithMethods(Class, Map)
     * @see #instanceWithConstructor(Class, Map)
     * @see #instanceWithEmptyConstructor(Class)
     * @see #instanceWithUnsafe(Class)
     */
    @Override
    public <T> T instance(Class<T> cls, Map<String, Object> params) throws InstancerError {
        try {
            if (SingletonInstances.isSingleton(cls)) {
                SLogger.LOGGER.debug("The class " + Utility.getClassName(cls) + " is singleton. Using the saved instance.");
                return SingletonInstances.getInstance(cls);
            }

            Optional<T> asInstantiable = instanceAsInstantiable(cls, params);
            if (asInstantiable.isPresent()) {
                SLogger.LOGGER.debug("The class " + Utility.getClassName(cls) + " has been instanced with Instantiable interface.");
                return asInstantiable.get();
            }

            Optional<T> withMethod = instanceWithMethods(cls, params);
            if (withMethod.isPresent()) {
                SLogger.LOGGER.debug("The class " + Utility.getClassName(cls) + " has been instanced with static a method.");
                return withMethod.get();
            }

            Optional<T> withOptionsMethod = getOptions().getInstanceMethod(cls);
            if (withOptionsMethod.isPresent()) {
                SLogger.LOGGER.debug("The class " + Utility.getClassName(cls) + " has been instanced with a provided instance method.");
                return withOptionsMethod.get();
            }

            if (!isAnInstantiableClass(cls))
                throw new InstancerError("The provided class " + Utility.getClassName(cls) + " is not instantiable.");

            Optional<T> withConstructor = instanceWithConstructor(cls, params);
            if (withConstructor.isPresent()) {
                SLogger.LOGGER.debug("The class " + Utility.getClassName(cls) + " has been instanced with a constructor.");
                return withConstructor.get();
            }

            Optional<T> withEmptyConstructor = instanceWithEmptyConstructor(cls);
            if (withEmptyConstructor.isPresent()) {
                SLogger.LOGGER.debug("The class " + Utility.getClassName(cls) + " has been instanced with the empty constructor.");
                return withEmptyConstructor.get();
            }

            Optional<T> withUnsafe = instanceWithUnsafe(cls);
            if (withUnsafe.isPresent()) {
                SLogger.LOGGER.debug("The class " + Utility.getClassName(cls) + " has been instanced with sun.misc.Unsafe internal class.");
                return withUnsafe.get();
            }
        } catch (Exception e) {
            throw new InstancerError("An error occurs while instancing " + Utility.getClassName(cls), e);
        }

        throw new InstancerError("No way of the supported one can instantiate: " + Utility.getClassName(cls));
    }

    @Override
    public InstancerOptions getOptions() {
        return options;
    }

    /**
     * Try to instantiate the {@code class} with the {@link Unsafe}
     * internal Java class.
     *
     * @param cls The class to instantiate.
     * @param <T> The class and instance type.
     * @return A {@code new instance} if no error occurs, otherwise {@link Optional#empty()}
     * @throws InstancerError If an error occurs while instancing the {@code class}.
     */
    @SuppressWarnings("unchecked")
    private <T> Optional<T> instanceWithUnsafe(Class<?> cls) throws InstancerError {
        try {
            return Optional.ofNullable((T) Unsafe.getUnsafe().allocateInstance(cls));
        } catch (InstantiationException e) {
            throw new InstancerError("An error occurs while instancing " + Utility.getClassName(cls) + " with sun.misc.Unsafe.", e);
        }
    }

    /**
     * Try to instance the {@code class} with an empty constructor.
     *
     * @param cls The class to instantiate.
     * @return A {@code new instance} if an empty constructor is found and no error occurs, otherwise {@link Optional#empty()}
     * @throws InstancerError If an error occurs while instancing the {@code class}.
     */
    private <T> Optional<T> instanceWithEmptyConstructor(Class<T> cls) throws InstancerError {
        try {
            Constructor<T> constructor = cls.getConstructor();
            constructor.setAccessible(true);
            return Optional.of(constructor.newInstance());
        } catch (NoSuchMethodException e) {
            throw new InstancerError("No empty constructor found.", e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InstancerError("An error occurs while instancing " + Utility.getClassName(cls) + " with empty constructor", e);
        }
    }

    /**
     * Try to instance the {@code class} as it is a {@link Instantiable}.
     *
     * @param cls    The class to instantiate.
     * @param params The params to provide to the method.
     * @param <T>    The class and instance type.
     * @return A {@code new instance} if {@link Instantiable} is assignable from class and no error occurs, otherwise {@link Optional#empty()}
     * @throws InstancerError If an error occurs while instancing the {@code class}.
     */
    @SuppressWarnings("unchecked")
    private <T> Optional<T> instanceAsInstantiable(Class<T> cls, Map<String, Object> params) throws InstancerError {
        if (!Wrappers.isAssignable(Instantiable.class, cls)) return Optional.empty();

        try {
            Method instantiate = getInstantiateMethod(cls);
            Object instance = instantiate.invoke(null, params);

            if (instance != null
                    && Wrappers.isAssignable(instance.getClass(), cls)) {
                return Optional.of((T) instance);
            }

            throw new InstancerError("The method \"instantiate\" does not return a instance of " + Utility.getClassName(cls));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new InstancerError("An error occurs while invoking method \"instantiate\" in " + Utility.getClassName(cls), e);
        }
    }

    /**
     * Get {@code instance} method specified in {@link Instantiable} interface.
     *
     * @param cls The class to get method from.
     * @return The {@link Instantiable#instantiate(Map)} method.
     */
    private Method getInstantiateMethod(Class<?> cls) {
        try {
            Method instantiate = cls.getDeclaredMethod("instantiate", Map.class);
            instantiate.setAccessible(true);
            return instantiate;
        } catch (NoSuchMethodException e) {
            // Should not be possible.
            throw new InstancerError("The " + Utility.getClassName(cls) +
                    " is assignable from Instantiable and the method \"instantiate\" is not declared, huh?", e);
        }
    }

    /**
     * Try to instance the {@code class} with any constructor
     * declared in class, including private, protected and default (package) access.
     * <p>
     * If any constructor is annotated with {@link Initialize} will be processed first.
     *
     * @param cls    The class to instantiate.
     * @param params The params to provide to the constructor.
     * @param <T>    The class and instance type.
     * @return A {@code new instance} if any method is found and no error occurs, otherwise {@link Optional#empty()}
     * @throws InstancerError If an error occurs while instancing the {@code class}.
     */
    private <T> Optional<T> instanceWithConstructor(Class<T> cls, Map<String, Object> params) throws
            InstancerError {
        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            ConstructorInstancer newConstructor = new ConstructorInstancer(constructor);
            try {
                return Optional.ofNullable(newConstructor.apply(cls, params));
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new InstancerError("An error occurs while instancing " + Utility.getClassName(cls) + " with " + constructor.getName() + "(" + String.join(", ", Arrays.stream(constructor.getParameterTypes()).map(Class::getSimpleName).toList()) + ")", e);
            }
        }
        return Optional.empty();
    }

    /**
     * Try to instance the {@code class} with any static method
     * that return an instance of the {@code class}.
     * <p>
     * If any method is annotated with {@link Initialize} will be processed first.
     *
     * @param cls    The class to instantiate
     * @param params The params to provide to the method.
     * @param <T>    The class and instance type.
     * @return A {@code new instance} if any method is found and no error occurs, otherwise {@link Optional#empty()}
     * @throws InstancerError If an error occurs while instancing the {@code class}.
     */
    private <T> Optional<T> instanceWithMethods(Class<T> cls, Map<String, Object> params) throws
            InstancerError {
        List<Method> methods = Arrays.stream(cls.getDeclaredMethods())
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .filter(m -> !m.getReturnType().equals(Class.class) && Wrappers.isAssignable(m.getReturnType(), cls))
                .toList();

        for (Method method : methods) {
            MethodInstancer newMethod = new MethodInstancer(method);
            try {
                return Optional.ofNullable(newMethod.apply(cls, params));
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new InstancerError("An error occurs while instancing " + Utility.getClassName(cls) + " with " + method.getName(), e);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if the {@code class} is instantiable
     * without any manual addition.
     *
     * @param cls The class to check
     * @return {@code true} if it is, otherwise {@code false}.
     */
    private boolean isAnInstantiableClass(Class<?> cls) {
        return !cls.isEnum() &&
                !cls.isInterface() &&
                !Modifier.isAbstract(cls.getModifiers());
    }

}
