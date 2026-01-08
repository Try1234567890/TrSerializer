package me.tr.trserializer.processes.process;

import me.tr.trlogger.levels.TrDebug;
import me.tr.trlogger.levels.TrError;
import me.tr.trlogger.levels.TrLevel;
import me.tr.trserializer.annotations.Ignore;
import me.tr.trserializer.annotations.includeIf.IncludeIf;
import me.tr.trserializer.annotations.includeIf.IncludeStrategy;
import me.tr.trserializer.annotations.naming.Naming;
import me.tr.trserializer.annotations.naming.NamingStrategy;
import me.tr.trserializer.exceptions.InstancerError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.instancers.ProcessInstancer;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.registries.HandlersRegistry;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    protected static final String CACHE_ID = "TrSerializer:!__#@ID__!";
    protected static final String CACHE_REF = "TrSerializer:!__#@REF__!";
    private final IdentityHashMap<TypeHandler, Map.Entry<Object, GenericType<?>>>
            RUNNING_HANDLERS = new IdentityHashMap<>();
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
     * @return a map of handlers currently being executed in the stack.
     */
    public IdentityHashMap<TypeHandler,
            Map.Entry<Object, GenericType<?>>> getRunningHandlers() {
        return RUNNING_HANDLERS;
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
                TrLogger.dbg("Executing \"" + addonName + "\"");

                Optional<?> result = addon.process(this, obj, type, field);

                if (result.isEmpty()) {
                    TrLogger.dbg("Process \"" + addonName + "\" returned empty");
                    continue;
                }

                return Optional.of(Map.entry(addon, makeReturn(obj, result.get(), type)));
            } catch (Exception e) {
                TrLogger.exception(new RuntimeException("The addon \"" + addonName + "\" thrown an exception.", e));
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
            TrLogger.exception(new TypeMissMatched("Params for result building are null."));
            return true;
        }
        final int objLen = obj.length;
        if (objLen < 4) {
            TrLogger.exception(new TypeMissMatched("Params for result building are not enough. Expected: 4, Found: " + objLen));
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
        if (!isValid(result).isSuccess())
            return null;

        cache(object, result);

        Class<?> expectedClass = Utility.getWrapper(type.getTypeClass());
        Class<?> resultClass = Utility.getWrapper(result.getClass());

        if (Object.class.isAssignableFrom(expectedClass)
                || resultClass.isAssignableFrom(expectedClass)) {
            return (T) result;
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

        TrLogger.exception(
                new TypeMissMatched("The result " + result + " is not assignable from " + expectedClass));
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
     * Invokes all registered 'end methods' (cleanup or finalization) for a given instance.
     *
     * @param instance the object instance on which to invoke the methods.
     */
    protected void executeEndMethods(Object instance) {
        if (instance == null) {
            TrLogger.exception(
                    new NullPointerException("Instance is null."));
            return;
        }
        executeMethods(instance, getEndMethods(instance.getClass(), getEndMethods()));
    }

    /**
     * Retrieves the mapping of classes to their respective end-method names for this process.
     * <p>
     * This is decoupled from {@link ProcessOptions} because {@code Serializer} and
     * {@code Deserializer} require different finalization logic.
     * </p>
     *
     * @return a map where keys are classes and values are arrays of method names; never {@code null}.
     * @implNote Implementations must return an empty map if no end methods are defined.
     */
    protected abstract Map<Class<?>, String[]> getEndMethods();

    /**
     * Invokes a set of methods on the specified instance via reflection.
     *
     * @param instance the target object.
     * @param methods  the methods to be invoked.
     */
    private void executeMethods(Object instance, Method[] methods) {
        if (instance == null) {
            TrLogger.exception(
                    new NullPointerException("Instance is null."));
            return;
        }

        if (methods == null) {
            TrLogger.exception(
                    new NullPointerException("Methods is null."));
            return;
        }

        for (Method method : methods) {
            try {
                method.setAccessible(true);
                method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                TrLogger.exception(new RuntimeException("An error occurs while invoking method " + method.getName() + " in class " + instance.getClass().getSimpleName(), e));
            }
        }
    }

    /**
     * Resolves method names into {@link Method} objects for a specific class.
     *
     * @param clazz      the class to inspect.
     * @param endMethods the map of class-to-method-names definitions.
     * @return an array of resolved {@code Method} objects; empty if none found or class not registered.
     */
    private Method[] getEndMethods(Class<?> clazz, Map<Class<?>, String[]> endMethods) {
        if (clazz == null) {
            TrLogger.exception(new NullPointerException("Class is null"));
            return new Method[0];
        }

        if (endMethods == null || endMethods.isEmpty() ||
                !endMethods.containsKey(clazz)) {
            return new Method[0];
        }

        String[] methodNames = endMethods.get(clazz);
        Method[] methods = new Method[methodNames.length];

        for (int i = 0; i < methodNames.length; i++) {
            String name = methodNames[i];
            Method method = getMethod(clazz, name);

            if (method == null) {
                TrLogger.exception(new NoSuchMethodException("No method founds in class " + clazz + " with name: " + name));
                continue;
            }

            methods[i] = method;
        }

        return methods;
    }

    /**
     * Recursively searches for a method by name in the class hierarchy.
     *
     * @param clazz the starting class for the search.
     * @param name  the name of the method.
     * @return the {@link Method} if found; {@code null} otherwise.
     */
    private Method getMethod(Class<?> clazz, String name) {
        if (clazz == null) {
            TrLogger.exception(new NullPointerException("The provided class is null."));
            return null;
        }

        if (name == null || name.isEmpty()) {
            TrLogger.exception(new NullPointerException("The provided method name is null."));
            return null;
        }

        Class<?> current = clazz;

        while (current != null && !current.equals(Object.class)) {
            try {
                return current.getDeclaredMethod(name);
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }

        TrLogger.exception(new NoSuchMethodException("No method found in class " + clazz + " with name: " + name));
        return null;
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
            TrLogger.exception(new InstancerError("An error occurs while instancing " + clazz + ": " + instancer.getReason()));
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
            TrLogger.exception(
                    new InstancerError("An error occurs while instancing " + clazz, instancer.getReason()));
            instancer.reset();
            return null;
        }

        return instance;
    }

    /**
     * Applies the naming strategy to a field's name based on {@link Naming} annotations.
     *
     * @param field the field whose name should be transformed.
     * @return the transformed field name.
     */
    protected String applyNamingStrategy(Field field) {
        String fieldName = field.getName();
        Optional<Naming> annotation = getNamingAnn(field);

        if (annotation.isPresent())
            return applyNamingStrategy(fieldName, annotation.get());

        return fieldName;
    }

    /**
     * Checks for the presence of a {@link Naming} annotation on the field or its declaring class.
     */
    private Optional<Naming> getNamingAnn(Field field) {
        if (field.isAnnotationPresent(Naming.class)) {
            return Optional.of(field.getAnnotation(Naming.class));
        }

        Class<?> declaring = field.getDeclaringClass();
        if (declaring.isAnnotationPresent(Naming.class)) {
            return Optional.of(declaring.getAnnotation(Naming.class));
        }

        return Optional.empty();
    }

    /**
     * Formats a name using the specified {@link Naming} annotation settings.
     *
     * @param name the original name.
     * @param ann  the naming annotation containing strategies.
     * @return the formatted name.
     */
    public String applyNamingStrategy(String name, Naming ann) {
        NamingStrategy strategy = ann.strategy();

        NamingStrategy from = ann.from();

        if (strategy == NamingStrategy.NOTHING) {
            TrLogger.warning("The strategy of @Naming on " + name + " is null. Ignoring it.");
            return name;
        }


        if (from == NamingStrategy.NOTHING)
            return strategy.format(name);

        return strategy.format(name, from.getFormat());
    }


    /**
     * Retrieves all valid fields from the provided class and its superclasses.
     * <p>
     * Fields are filtered based on the logic in {@link #isValid(Field, Object)}.
     * </p>
     *
     * @param clazz the class to inspect.
     * @return a {@code Set} containing all accessible and valid {@link Field} objects.
     */
    public Set<Field> getFields(Class<?> clazz) {
        Set<Field> result = new HashSet<>();

        if (clazz == null) {
            TrLogger.exception(
                    new NullPointerException("The provide class is null."));
            return result;
        }

        Class<?> current = clazz;
        while (current != null && !current.equals(Object.class)) {
            Arrays.stream(current.getDeclaredFields())
                    .filter(f -> isValid(f).isSuccess())
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

    /*
     * ===============================
     * VALIDATION CHECKS FOR HANDLERS
     * ===============================
     */

    /**
     * Determines if the package of the provided object is restricted by process options.
     */
    private ValidationResult isPackageBlocked(Object o) {
        if (o == null)
            return ValidationResult.fatal("The provided object is null.");


        if (getOptions().isPackageBlocked(o.getClass().getPackageName())) {
            return ValidationResult.error("The provided object package is blocked.");
        }

        return ValidationResult.success();
    }

    /**
     * Validates if an object and its generic type are eligible for processing.
     *
     * @param obj  the object to validate.
     * @param type the generic type metadata.
     * @return the {@link ValidationResult} of the check.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected ValidationResult isValid(Object obj, GenericType<?> type) {
        if (type == null)
            return ValidationResult.fatal("The provided object is null.");

        return isValid(obj);
    }

    /**
     * Validates if an object is eligible for processing based on nullability and process options.
     *
     * @param obj the object to validate.
     * @return a {@link ValidationResult} indicating success or the severity of the failure.
     */
    protected ValidationResult isValid(Object obj) {
        if (obj == null) {
            if (!getOptions().isAcceptNulls())
                return ValidationResult.fatal("The provided object is null and the process not accept null values.");
            return ValidationResult.error("The provided object is null but the process accepts null values.");
        }


        if (obj instanceof Optional<?> opt && opt.isEmpty()) {
            if (!getOptions().isAcceptEmptyOptional())
                return ValidationResult.fatal("The provided object is an empty optional and the process not accept empty optional values.");
            return ValidationResult.error("The provided object is an empty optional but the process accepts empty optional values.");
        }

        return isPackageBlocked(obj);
    }

    /**
     * Validates if a field should be included in the process.
     * <p>
     * A field is considered invalid if it is annotated with {@link Ignore},
     * fails a {@link IncludeIf} strategy, or violates modifier-based rules
     * defined in {@link ProcessOptions}.
     * </p>
     *
     * @param field the field to validate.
     * @param value the current value of the field.
     * @return a {@link ValidationResult} representing the validation state.
     */
    protected ValidationResult isValid(Field field, Object value) {
        if (field == null) {
            return ValidationResult.fatal("The provided field is null.");
        }

        String fieldName = field.getName();

        if (field.isAnnotationPresent(IncludeIf.class)) {
            IncludeIf ann = field.getAnnotation(IncludeIf.class);
            IncludeStrategy strategy = ann.strategy();


            if (strategy == null) {
                return ValidationResult.fatal("The strategy of @IncludeIf on " + fieldName + " in class " + Utility.getClassName(field.getDeclaringClass()) + " is null. Ignoring it.");
            }

            if (!strategy.isValid(value)) {
                return ValidationResult.error("The value of " + fieldName + " doesn't pass the @IncludeIf strategy check. Skipping it...");
            }
        }


        return isValid(field);
    }

    protected ValidationResult isValid(Field field) {
        String fieldName = field.getName();

        int mod = field.getModifiers();
        if (field.isAnnotationPresent(Ignore.class)) {
            return ValidationResult.error("The field " + fieldName + " is annotated with @Ignore.");
        }
        if (getOptions().isIgnoreStatic() && Modifier.isStatic(mod)) {
            return ValidationResult.error("The option \"ignore static\" is enabled and " + fieldName + " is static");
        }
        if (getOptions().isIgnoreFinal() && Modifier.isFinal(mod)) {
            return ValidationResult.error("The option \"ignore final\" is enabled and " + fieldName + " is final");
        }
        if (getOptions().isIgnoreTransient() && Modifier.isTransient(mod)) {
            return ValidationResult.error("The option \"ignore transient\" is enabled and " + fieldName + " is transient");
        }

        return ValidationResult.success();
    }

    /**
     * Represents the outcome of a validation check, including success status and logging info.
     */
    public static class ValidationResult {
        private final boolean success;
        private final String message;
        private final TrLevel level;


        private ValidationResult(boolean success, String message, TrLevel level) {
            this.success = success;
            this.message = message;
            this.level = level;
        }

        /**
         * Returns a successful result with no message.
         */
        public static ValidationResult success() {
            return new ValidationResult(true, "", null);
        }

        /**
         * Returns a failed result that should be logged as an error.
         *
         * @param msg the error message.
         */
        public static ValidationResult fatal(String msg) {
            return new ValidationResult(false, msg, TrError.ERROR);
        }

        /**
         * Returns a failed result that should be logged as a debug message.
         *
         * @param msg the debug/info message.
         */
        public static ValidationResult error(String msg) {
            return new ValidationResult(false, msg, TrDebug.DEBUG);
        }

        /**
         * Prints the message to the logger if a log level is assigned.
         */
        public void print() {
            if (level != null)
                TrLogger.getInstance().log(message(), level);
        }

        /**
         * Prints the result and returns the success status.
         *
         * @return {@code true} if validation passed.
         */
        public boolean isSuccess() {
            print();
            return success;
        }

        /**
         * @return the validation message.
         */
        public String message() {
            return message;
        }
    }
}