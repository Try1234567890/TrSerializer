package me.tr.trserializer.processes.process;

import me.tr.trserializer.exceptions.InstancerError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.instancers.ProcessInstancer;
import me.tr.trserializer.logger.ProcessLogger;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.processes.process.helper.MethodsExecutor;
import me.tr.trserializer.processes.process.helper.NamingStrategyApplier;
import me.tr.trserializer.processes.process.helper.ProcessValidator;
import me.tr.trserializer.registries.HandlersRegistry;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Base abstract class for all serialization and deserialization processes.
 *
 * <p>A process orchestrates the transformation of objects by managing:
 * <ul>
 * <li>{@link ProcessOptions}: Configuration settings that dictate process behavior.</li>
 * <li>{@link ProcessInstancer}: Responsible for instantiating classes during the process.</li>
 * <li>{@link ProcessCache}: Prevents {@code StackOverflowError} and maintains object identity/references.</li>
 * </ul>
 */
public abstract class Process {
    //protected static final String CACHE_ID = "TrSerializer:!__#@ID__!";
    //protected static final String CACHE_REF = "TrSerializer:!__#@REF__!";
    private ProcessContext context;

    /**
     * @return the current {@link ProcessContext} containing shared process resources.
     */
    public ProcessContext getContext() {
        return context;
    }

    /**
     * Sets the context for this process.
     *
     * @param context the context to assign.
     */
    protected void setContext(ProcessContext context) {
        this.context = context;
    }

    /**
     * Retrieves the instancer with the provided parameters.
     *
     * @param params map of parameters for instantiation.
     * @return the {@link ProcessInstancer} instance.
     */
    public ProcessInstancer getInstancer(Map<String, Object> params) {
        return getContext().getInstancer(params);
    }

    /**
     * @return the configuration options for this process.
     */
    public ProcessOptions getOptions() {
        return getContext().getOptions();
    }

    /**
     * @return the cache system used to track processed objects.
     */
    public ProcessCache getCache() {
        return getContext().getCache();
    }

    /**
     * @return the logger for this process
     */
    public ProcessLogger getLogger() {
        return getContext().getLogger();
    }

    /**
     * @return The naming strategy applier for this process
     */
    public NamingStrategyApplier getNamingStrategyApplier() {
        return getContext().getNamingStrategyApplier();
    }

    /**
     * @return The process validator for this process.
     */
    public ProcessValidator getProcessValidator() {
        return getContext().getProcessValidator();
    }

    /**
     * @return The methods executor for this process.
     */
    public MethodsExecutor getMethodsExecutor() {
        return getContext().getMethodsExecutor();
    }


    /*
     * ===============================
     * UTILITY FOR HANDLERS
     * ===============================
     */

    /**
     * Executes all registered addons for this process sequentially.
     *
     * @param obj   the object to be processed by addons.
     * @param type  the generic type metadata of the object.
     * @param field the field associated with the object, or {@code null} if not applicable.
     * @return an {@link Optional} containing the first addon that returns a non-empty result,
     * paired with its processed output.
     */
    protected Optional<Map.Entry<PAddon, ?>> processAddons(Object obj, GenericType<?> type, Field field) {
        for (PAddon addon : getContext().getAddons()) {
            String addonName = addon.getName();
            try {
                getLogger().debug("Executing \"" + addonName + "\"");

                Optional<?> result = addon.process(this, obj, type, field);

                if (result.isEmpty()) {
                    getLogger().debug("Process \"" + addonName + "\" returned empty");
                    continue;
                }

                return Optional.of(Map.entry(addon, makeReturn(obj, result.get(), type)));
            } catch (Exception e) {
                getLogger().throwable(new RuntimeException("The addon \"" + addonName + "\" thrown an exception.", e));
            }
        }

        return Optional.empty();
    }

    /**
     * Validates if the array of parameters required to build a process result is consistent.
     *
     * @param obj the array of parameters to check.
     * @return {@code true} if parameters are null or insufficient; {@code false} otherwise.
     */
    protected boolean isParamsOfResultInvalid(Object[] obj) {
        if (obj == null) {
            getLogger().throwable(new TypeMissMatched("Params for result building are null."));
            return true;
        }
        final int objLen = obj.length;
        if (objLen < 4) {
            getLogger().throwable(new TypeMissMatched("Params for result building are not enough. Expected: 4, Found: " + objLen));
            return true;
        }
        return false;
    }


    /**
     * Finalizes the process result by verifying type compatibility and caching the result.
     * <p>
     * This method ensures the produced value matches the expected {@code GenericType}.
     * If compatible, the result is cached. If incompatible, it attempts basic wrapping
     * (Optional, List, Map) or throws an exception.
     * </p>
     *
     * @param <T>    the expected return type.
     * @param object the original source object.
     * @param result the output produced by the process.
     * @param type   the expected type metadata.
     * @return the casted result if valid, or {@code null} if validation fails.
     */
    @SuppressWarnings("unchecked")
    protected <T> T makeReturn(Object object, Object result, GenericType<T> type) {
        if (!getProcessValidator().isValid(result).isSuccess())
            return null;

        cache(object, result);

        Class<?> expectedClass = Utility.getWrapper(type.getTypeClass());
        Class<?> resultClass = Utility.getWrapper(result.getClass());

        if (Object.class.isAssignableFrom(expectedClass)
                || resultClass.isAssignableFrom(expectedClass)) {
            return (T) result;
        }

        if (String.class.isAssignableFrom(resultClass)) {
            return (T) String.valueOf(result);
        }

        if (Optional.class.isAssignableFrom(resultClass)) {
            return (T) Optional.of(result);
        }

        if (Collection.class.isAssignableFrom(resultClass)) {
            return (T) new ArrayList<>(List.of(result));
        }

        if (Map.class.isAssignableFrom(resultClass)) {
            return (T) new HashMap<>(Map.of("", (T) result));
        }

        getLogger().throwable(new TypeMissMatched("The result " + result + " is not assignable from " + expectedClass));
        return null;
    }

    /**
     * Maps the source object to its processed result in the {@link ProcessCache}.
     * <p>
     * Caching only occurs if the objects are eligible (cachable) and the source
     * is not already present in the cache.
     * </p>
     *
     * @param object the original primitive/source object.
     * @param result the processed/target object.
     */
    protected void cache(Object object, Object result) {
        if (getCache().isCachable(object, result) &&
                !getCache().has(object)) {
            getCache().put(object, result);
        }
    }

    /**
     * Creates a new instance of the specified class using the current instancer and parameters.
     *
     * @param clazz the class to instantiate.
     * @param map   the parameters for initialization.
     * @return the new instance, or {@code null} if instantiation fails.
     */
    public Object instance(Class<?> clazz, Map<String, Object> map) {
        ProcessInstancer instancer = getInstancer(map);

        Object instance = instancer.instance(clazz);

        if (instance == null) {
            getLogger().throwable(new InstancerError("An error occurs while instancing " + clazz + ": " + instancer.getReason()));
            instancer.reset();
            return null;
        }

        return instance;
    }

    /**
     * Creates a new instance of the specified class using default parameters.
     *
     * @param clazz the class to instantiate.
     * @return the new instance, or {@code null} if instantiation fails.
     */
    public Object instance(Class<?> clazz) {
        ProcessInstancer instancer = getInstancer(new HashMap<>());
        Object instance = instancer.instance(clazz);

        if (instance == null) {
            getLogger().throwable(
                    new InstancerError("An error occurs while instancing " + clazz, instancer.getReason()));
            instancer.reset();
            return null;
        }

        return instance;
    }

    /**
     * Retrieves all valid fields from the provided class and its superclasses.
     * <p>
     * Fields are filtered based on the logic in {@link #getProcessValidator#isValid(Field, Object)}.
     * </p>
     *
     * @param clazz the class to inspect.
     * @return a {@code Set} containing all accessible and valid {@link Field} objects.
     */
    public Set<Field> getFields(Class<?> clazz) {
        Set<Field> result = new HashSet<>();

        if (clazz == null) {
            getLogger().throwable(new NullPointerException("The provide class is null."));
            return result;
        }

        Class<?> current = clazz;
        while (current != null && !current.equals(Object.class)) {

            Arrays.stream(current.getDeclaredFields())
                    .filter(f -> getProcessValidator().isValid(f).isSuccess())
                    .forEach(result::add);

            current = current.getSuperclass();
        }

        return result;
    }

    /**
     * Retrieves the appropriate {@link TypeHandler} for the given class from the registry.
     *
     * @param clazz the class to find a handler for.
     * @return an {@link Optional} containing the handler, or empty if not found or class is null.
     */
    public Optional<TypeHandler> getHandler(Class<?> clazz) {
        // No null-checks needed, simply if clazz is null returns Optional.empty().
        return HandlersRegistry.getInstance().get(clazz, this);
    }
}