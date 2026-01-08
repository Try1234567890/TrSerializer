package me.tr.trserializer.processes.process;

import me.tr.trserializer.logger.Logger;
import me.tr.trserializer.processes.options.Option;
import me.tr.trserializer.processes.options.Options;
import me.tr.trserializer.utility.Utility;

import java.util.*;
import java.util.function.Supplier;

/**
 * This class contains all configurations options for processes.
 */
public class ProcessOptions {
    private final Process process;
    // Instances is a Supplier that returns a new instance of the class it is associated with
    private final Instances instances = new Instances();
    // The process will stop if it encounters a class from a package included in this Set.
    private final BlockedPackages blockedPackages = new BlockedPackages();
    // If this is true, processes recognize nulls as valid value.
    private final Option<Boolean> acceptNulls = new Option<>(Options.ACCEPT_NULLS, true);
    // If this is true, processes recognize empty optional as valid value.
    private final Option<Boolean> acceptEmptyOptional = new Option<>(Options.ACCEPT_EMPTY_OPTIONAL, true);
    // If this is true, boolean will be processed as 1 or 0.
    private final Option<Boolean> useNumericBoolean = new Option<>(Options.USE_NUMERIC_BOOLEAN, false);
    // If this is true, character will be processed with their ID in ASCII.
    private final Option<Boolean> useNumericCharacter = new Option<>(Options.USE_NUMERIC_CHARACTER, false);
    /*
     * If this is true, the cache will be used.
     * <p>
     * This is used to prevent duplicates instances and to preserve object identity, if enabled.
     * If disabled, this will be used only to prevent StackOverFlow.
     *
     * NOT IMPLEMENTED FEATURE.
     */
    private final Option<Boolean> useCache = new Option<>(Options.USE_CACHE, true);
    // If this is true, the transient fields will be ignored.
    private final Option<Boolean> ignoreTransient = new Option<>(Options.EXCLUDE_TRANSIENT_FIELDS, true);
    // If this is true, the static fields will be ignored.
    private final Option<Boolean> ignoreStatic = new Option<>(Options.IGNORE_STATIC, true);
    // If this is true, the final fields will be ignored.
    private final Option<Boolean> ignoreFinal = new Option<>(Options.IGNORE_FINAL, false);
    // This map contains the ends methods that at the of the processing the class (or the key of the map) will be executed.
    private final Option<Map<Class<?>, String[]>> endMethods = new Option<>(Options.END_METHODS, new HashMap<>());
    // This map contains the start methods that at the of the processing the class (or the key of the map) will be executed.
    private final Option<Map<Class<?>, String[]>> startMethods = new Option<>(Options.START_METHODS, new HashMap<>());

    public ProcessOptions(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    /*
     * =----------------=
     * START METHODS OPTION
     * =----------------=
     */
    public Option<Map<Class<?>, String[]>> getStartMethodsOptions() {
        return startMethods;
    }

    /**
     * Retrieve the methods that will be executed at the
     * deserialization start of the class.
     *
     * @return All start methods register for this deserializer.
     */
    public Map<Class<?>, String[]> getStartMethods() {
        return getStartMethodsOptions().getValue();
    }

    /**
     * Checks if the class has start methods registered.
     *
     * @param clazz The class to check for.
     * @return {@code true} if it has, otherwise {@code false}.
     */
    public boolean hasStartMethods(Class<?> clazz) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return false;
        }

        return getStartMethods().containsKey(clazz);
    }

    /**
     * Retrieve the start method names for the class,
     * if it has any.
     *
     * @param clazz The class to get method names for.
     * @return The method names if it has any, otherwise {@code null}.
     * @throws NullPointerException if the class is null
     */
    public String[] getStartMethodNamesFor(Class<?> clazz) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return null;
        }
        return getStartMethods().get(clazz);
    }

    public ProcessOptions addStartMethod(Class<?> clazz, String method) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return this;
        }
        if (method == null) {
            getProcess().getLogger().throwable(new NullPointerException("The method is null."));
            return this;
        }

        getStartMethods().put(clazz, new String[]{method});
        return this;
    }

    public ProcessOptions addStartMethods(Class<?> clazz, String... methods) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return this;
        }

        if (methods == null || methods.length == 0) {
            getProcess().getLogger().throwable(new NullPointerException("The methods are null."));
            return this;
        }

        getStartMethods().put(clazz, methods);
        return this;
    }


    /*
     * =----------------=
     * END METHODS OPTION
     * =----------------=
     */
    public Option<Map<Class<?>, String[]>> getEndMethodsOptions() {
        return endMethods;
    }

    /**
     * Retrieve the methods that will be executed at the
     * deserialization end of the class.
     *
     * @return All ends methods register for this deserializer.
     */
    public Map<Class<?>, String[]> getEndMethods() {
        return getEndMethodsOptions().getValue();
    }

    /**
     * Checks if the class has end methods registered.
     *
     * @param clazz The class to check for.
     * @return {@code true} if it has, otherwise {@code false}.
     */
    public boolean hasEndMethods(Class<?> clazz) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return false;
        }

        return getEndMethods().containsKey(clazz);
    }

    /**
     * Retrieve the end method names for the class,
     * if it has any.
     *
     * @param clazz The class to get method names for.
     * @return The method names if it has any, otherwise {@code null}.
     * @throws NullPointerException if the class is null
     */
    public String[] getEndMethodNamesFor(Class<?> clazz) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return null;
        }
        return getEndMethods().get(clazz);
    }

    public ProcessOptions addEndMethod(Class<?> clazz, String method) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return this;
        }
        if (method == null) {
            getProcess().getLogger().throwable(new NullPointerException("The method is null."));
            return this;
        }

        getEndMethods().put(clazz, new String[]{method});
        return this;
    }

    public ProcessOptions addEndMethods(Class<?> clazz, String... methods) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return this;
        }
        if (methods == null || methods.length == 0) {
            getProcess().getLogger().throwable(new NullPointerException("The methods are null."));
            return this;
        }

        getEndMethods().put(clazz, methods);
        return this;
    }


    /*
     * =-------------------=
     * IGNORE STATIC OPTIONS
     * =-------------------=
     */

    public Option<Boolean> getIgnoreStatic() {
        return ignoreStatic;
    }

    public boolean isIgnoreStatic() {
        return getIgnoreStatic().getValue();
    }

    public ProcessOptions setIgnoreStatic(boolean ignoreStatic) {
        getIgnoreStatic().setValue(ignoreStatic);
        return this;
    }


    /*
     * =-----------------=
     * IGNORE FINAL OPTIONS
     * =-----------------=
     */

    public Option<Boolean> getIgnoreFinal() {
        return ignoreFinal;
    }

    public boolean isIgnoreFinal() {
        return getIgnoreFinal().getValue();
    }

    public ProcessOptions setIgnoreFinal(boolean ignoreFinal) {
        getIgnoreFinal().setValue(ignoreFinal);
        return this;
    }

    /*
     * =-----------------=
     * IGNORE TRANSIENT OPTIONS
     * =-----------------=
     */
    public Option<Boolean> getIgnoreTransient() {
        return ignoreTransient;
    }

    public boolean isIgnoreTransient() {
        return getIgnoreTransient().getValue();
    }

    public ProcessOptions setIgnoreTransient(boolean value) {
        getIgnoreTransient().setValue(value);
        return this;
    }

    /*
     * =-----------------=
     * USE CACHE OPTIONS
     * =-----------------=
     */

    public Option<Boolean> getUseCache() {
        return useCache;
    }

    public boolean isUseCache() {
        return getUseCache().getValue();
    }

    public ProcessOptions setUseCache(boolean useCache) {
        getUseCache().setValue(useCache);
        return this;
    }


    /*
     * =-----------------=
     * BLOCKED PACKAGES OPTIONS
     * =-----------------=
     */

    public BlockedPackages getBlockedPackages() {
        return blockedPackages;
    }

    public boolean isPackageBlocked(String packageName) {
        return getBlockedPackages().isBlocked(packageName);
    }

    public ProcessOptions blockPackage(String packageName) {
        getBlockedPackages().block(packageName);
        return this;
    }

    public ProcessOptions unblockPackage(String packageName) {
        getBlockedPackages().unblock(packageName);
        return this;
    }

    public ProcessOptions blockPackages(String... packageNames) {
        Arrays.stream(packageNames).forEach(p -> getBlockedPackages().block(p));
        return this;
    }

    public ProcessOptions unblockPackages(String... packageNames) {
        Arrays.stream(packageNames).forEach(p -> getBlockedPackages().unblock(p));
        return this;
    }

    public ProcessOptions blockPackages(Collection<String> packageNames) {
        packageNames.forEach(p -> getBlockedPackages().block(p));
        return this;
    }

    public ProcessOptions unblockPackages(Collection<String> packageNames) {
        packageNames.forEach(p -> getBlockedPackages().unblock(p));
        return this;
    }

    /*
     * =-----------------=
     * USE NUMERIC BOOLEAN OPTIONS
     * =-----------------=
     */

    public Option<Boolean> getUseNumericBoolean() {
        return useNumericBoolean;
    }

    public boolean isUseNumericBoolean() {
        return getUseNumericBoolean().getValue();
    }

    public ProcessOptions setUseNumericBoolean(boolean value) {
        getUseNumericBoolean().setValue(value);
        return this;
    }


    /*
     * =-----------------=
     * USE NUMERIC CHARACTER OPTIONS
     * =-----------------=
     */

    public Option<Boolean> getUseNumericCharacter() {
        return useNumericCharacter;
    }

    public boolean isUseNumericCharacter() {
        return getUseNumericCharacter().getValue();
    }

    public ProcessOptions setUseNumericCharacter(boolean value) {
        getUseNumericCharacter().setValue(value);
        return this;
    }

    /*
     * =-----------------=
     * EMPTY OPTIONAL OPTIONS
     * =-----------------=
     */

    public Option<Boolean> getAcceptEmptyOptional() {
        return acceptEmptyOptional;
    }

    public boolean isAcceptEmptyOptional() {
        return getAcceptEmptyOptional().getValue();
    }

    public ProcessOptions setAcceptEmptyOptional(boolean value) {
        getAcceptEmptyOptional().setValue(value);
        return this;
    }

    /*
     * =-----------------=
     * NULLS OPTIONS
     * =-----------------=
     */

    public Option<Boolean> getAcceptNulls() {
        return acceptNulls;
    }

    public boolean isAcceptNulls() {
        return getAcceptNulls().getValue();
    }

    public ProcessOptions setAcceptNulls(boolean value) {
        getAcceptNulls().setValue(value);
        return this;
    }


    /*
     * =-----------------=
     * INSTANCES OPTIONS
     * =-----------------=
     */

    public Instances getInstances() {
        return instances;
    }

    public ProcessOptions addInstance(Class<?> clazz, Supplier<?> supplier) {
        getInstances().addInstance(clazz, supplier);
        return this;
    }

    public ProcessOptions removeInstance(Class<?> clazz) {
        getInstances().removeInstance(clazz);
        return this;
    }

    public boolean hasInstance(Class<?> clazz) {
        return getInstances().hasInstance(clazz);
    }

    public Object getInstance(Class<?> clazz) {
        return getInstances().getInstance(clazz);
    }

    public static class BlockedPackages extends Option<Set<String>> {

        public BlockedPackages() {
            super(Options.BLOCKED_PACKAGES, new HashSet<>());
        }

        public void block(String packageName) {
            if (packageName == null || packageName.isEmpty()) {
                Logger.exception(new NullPointerException("The package is null or empty."));
                return;
            }
            getValue().add(packageName);
        }

        public void unblock(String packageName) {
            if (packageName == null || packageName.isEmpty()) {
                Logger.exception(new NullPointerException("The package is null or empty."));
                return;
            }
            getValue().remove(packageName);
        }

        public boolean isBlocked(String packageName) {
            if (packageName == null || packageName.isEmpty()) {
                Logger.exception(new NullPointerException("The package is null or empty."));
                return false;
            }
            return getValue().contains(packageName);
        }
    }

    public static class Instances extends Option<HashMap<Class<?>, Supplier<?>>> {

        public Instances() {
            super(Options.DEFAULT_INSTANCES, new HashMap<>());
        }

        public void addInstance(Class<?> clazz, Supplier<?> supplier) {
            if (clazz == null) {
                Logger.exception(new NullPointerException("The provided class is null."));
                return;
            }

            if (supplier == null) {
                Logger.exception(new NullPointerException("The provided supplier is null."));
                return;
            }
            getValue().put(clazz, supplier);
        }

        public void removeInstance(Class<?> clazz) {
            if (clazz == null) {
                Logger.exception(new NullPointerException("The provided class is null."));
                return;
            }
            getValue().remove(clazz);
        }

        public boolean hasInstance(Class<?> clazz) {
            if (clazz == null) {
                Logger.exception(new NullPointerException("The provided class is null."));
                return false;
            }
            return getValue().containsKey(clazz);
        }

        public Supplier<?> getSupplier(Class<?> clazz) {
            if (clazz == null) {
                Logger.exception(new NullPointerException("The provided class is null."));
                return null;
            }
            return getValue().get(clazz);
        }

        public Object getInstance(Class<?> clazz) {
            Supplier<?> supplier = getSupplier(clazz);
            if (supplier == null) {
                Logger.dbg("No instance for " + Utility.getClassName(clazz) + " is found");
                return null;
            }
            return supplier.get();
        }
    }
}
