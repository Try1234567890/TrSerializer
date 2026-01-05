package me.tr.trserializer.processes;

import me.tr.trserializer.processes.options.Option;
import me.tr.trserializer.processes.options.Options;

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
    // If this is false, the all object is processes as complex object
    private final Option<Boolean> useHandlers = new Option<>(Options.USE_HANDLERS, true);
    public ProcessOptions(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    /*
     * =---------------------=
     * IGNORE HANDLERS OPTIONS
     * =---------------------=
     */

    public Option<Boolean> getUseHandlers() {
        return useHandlers;
    }

    public boolean isUseHandlers() {
        return getUseHandlers().getValue();
    }

    public ProcessOptions setUseHandlers(boolean ignoreHandlers) {
        getUseHandlers().setValue(ignoreHandlers);
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
            getValue().add(packageName);
        }

        public void unblock(String packageName) {
            getValue().remove(packageName);
        }

        public boolean isBlocked(String packageName) {
            return getValue().contains(packageName);
        }
    }

    public static class Instances extends Option<HashMap<Class<?>, Supplier<?>>> {

        public Instances() {
            super(Options.DEFAULT_INSTANCES, new HashMap<>());
        }

        public void addInstance(Class<?> clazz, Supplier<?> supplier) {
            getValue().put(clazz, supplier);
        }

        public void removeInstance(Class<?> clazz) {
            getValue().remove(clazz);
        }

        public boolean hasInstance(Class<?> clazz) {
            return getValue().containsKey(clazz);
        }

        public Supplier<?> getSupplier(Class<?> clazz) {
            return getValue().get(clazz);
        }

        public Object getInstance(Class<?> clazz) {
            Supplier<?> supplier = getSupplier(clazz);
            if (supplier != null)
                return supplier.get();
            return null;
        }
    }
}
