package me.tr.trserializer.processes.deserializer;

import me.tr.trserializer.processes.options.Option;
import me.tr.trserializer.processes.options.Options;
import me.tr.trserializer.processes.process.ProcessOptions;
import me.tr.trserializer.utility.Three;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class DeserializerOptions extends ProcessOptions {
    private final Option<Boolean> ignoreCase = new Option<>(Options.IGNORE_CASE, true);
    private final Option<List<Three<Class<?>, String, String[]>>> aliases = new Option<>(Options.ALIASES, new ArrayList<>());
    // If a class is inside this map as key, while processing it if
    // the function returns a different type, the new one will be used.
    private final Option<Map<Class<?>, Function<Object, Optional<Class<?>>>>> alternatives = new Option<>(Options.TYPE_ALTERNATIVES, new HashMap<>());

    public DeserializerOptions(Deserializer process) {
        super(process);
    }

    /*
     * =---------------------=
     * ALTERNATIVES OPTIONS
     * =---------------------=
     */

    public Option<Map<Class<?>, Function<Object, Optional<Class<?>>>>> getAlternativesOptions() {
        return alternatives;
    }

    public Map<Class<?>, Function<Object, Optional<Class<?>>>> getAlternatives() {
        return getAlternativesOptions().getValue();
    }

    public boolean hasAlternatives(Class<?> clazz) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return false;
        }
        return getAlternatives().containsKey(clazz);
    }

    /**
     * Retrieve the alternative logic of the provided class
     * if it has any.
     *
     * @param clazz The class to get logic for.
     * @return The alternative if it has any, otherwise {@code null}.
     * @throws NullPointerException if the class is null
     */
    public Function<Object, Optional<Class<?>>> getAlternatives(Class<?> clazz) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return null;
        }
        return getAlternatives().get(clazz);
    }

    /**
     * Add an alternatives for the class.
     *
     * @param clazz    The class to add alternative to.
     * @param function The function that contains logic to determinate which alternative use.
     */
    public DeserializerOptions addAlternatives(Class<?> clazz, Function<Object, Optional<Class<?>>> function) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return this;
        }

        if (function == null) {
            getProcess().getLogger().throwable(new NullPointerException("The function is null."));
            return this;
        }
        getAlternatives().put(clazz, function);
        return this;
    }

    /*
     * =----------------=
     *   ALIASES OPTION
     * =----------------=
     */

    /**
     * @see me.tr.trserializer.annotations.Aliases
     */
    @Deprecated(forRemoval = true)
    public Option<List<Three<Class<?>, String, String[]>>> getAliasesOption() {
        return aliases;
    }

    /**
     * Retrieve all aliases registered in this deserializer.
     *
     * @return all known aliases.
     * @see me.tr.trserializer.annotations.Aliases
     */
    @Deprecated(forRemoval = true)
    public List<Three<Class<?>, String, String[]>> getAliases() {
        return getAliasesOption().getValue();
    }

    /**
     * Add a new alias to this deserializer.
     *
     * @param declaringClazz The class that contains the field. (all classes if {@code null})
     * @param fieldName      The field name to register alias to.
     * @param aliases        The aliases to register.
     * @see me.tr.trserializer.annotations.Aliases
     */
    @Deprecated(forRemoval = true)
    public DeserializerOptions addAlias(Class<?> declaringClazz, String fieldName, String... aliases) {
        if (fieldName == null) {
            getProcess().getLogger().throwable(new NullPointerException("The field name is null."));
            return this;
        }
        if (aliases == null || aliases.length == 0) {
            getProcess().getLogger().throwable(new NullPointerException("The aliases are null."));
            return this;
        }
        getAliasesOption().getValue().add(new Three<>(declaringClazz, fieldName, aliases));
        return this;
    }

    /**
     * Add a new alias to this deserializer.
     *
     * @param alias A tree object containing all information for alias.
     * @see #addAlias(Class, String, String...)
     * @see me.tr.trserializer.annotations.Aliases
     */
    @Deprecated(forRemoval = true)
    public DeserializerOptions addAlias(Three<Class<?>, String, String[]> alias) {
        if (alias == null) {
            getProcess().getLogger().throwable(new NullPointerException("The alias is null."));
            return this;
        }
        getAliasesOption().getValue().add(alias);
        return this;
    }

    /**
     * Checks if the class has aliases.
     *
     * @param clazz The class to check for.
     * @return {@code true} if it has, otherwise {@code false}.
     * @see me.tr.trserializer.annotations.Aliases
     */
    @Deprecated(forRemoval = true)
    public boolean hasAliases(Class<?> clazz) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return false;
        }

        return getAliases().stream().anyMatch(alias -> alias.key().equals(clazz));
    }

    /**
     * Checks if the field has aliases.
     *
     * @param clazz     The class that contains the field.
     * @param fieldName The field name to check for.
     * @return {@code true} if it has, otherwise {@code false}.
     * @see me.tr.trserializer.annotations.Aliases
     */
    @Deprecated(forRemoval = true)
    public boolean hasAliases(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            getProcess().getLogger().throwable(new NullPointerException("The class is null."));
            return false;
        }

        if (fieldName == null || fieldName.isEmpty()) {
            getProcess().getLogger().throwable(new NullPointerException("The field name is null."));
            return false;
        }

        return hasAliases(clazz) && getAliases().stream().anyMatch(alias -> compare(alias.value(), fieldName));
    }

    /**
     * Checks if the field has aliases.
     *
     * @param field The field name to check for.
     * @return {@code true} if it has, otherwise {@code false}.
     * @see me.tr.trserializer.annotations.Aliases
     */
    @Deprecated(forRemoval = true)
    public boolean hasAliases(Field field) {
        if (field == null) {
            getProcess().getLogger().throwable(new NullPointerException("The field is null."));
            return false;
        }

        return hasAliases(field.getDeclaringClass(), field.getName());
    }

    /*
     * =----------------=
     * IGNORE CASE OPTION
     * =----------------=
     */
    public Option<Boolean> getIgnoreCase() {
        return ignoreCase;
    }

    /**
     * @return {@code true} if is "ignore case" enabled, otherwise {@code false}.
     */
    public boolean isIgnoreCase() {
        return getIgnoreCase().getValue();
    }

    /**
     * Enable the "ignore case" for this deserializer.
     * <p>
     * If ignore case is enabled, when picking value from the map for complex object,
     * the key case is ignored.
     *
     * @param ignoreCase The value to assign.
     */
    public DeserializerOptions setIgnoreCase(boolean ignoreCase) {
        getIgnoreCase().setValue(ignoreCase);
        return this;
    }

    private boolean compare(String first, String second) {
        return isIgnoreCase() ? first.equalsIgnoreCase(second) : first.equals(second);
    }

    public Deserializer getDeserializer() {
        return (Deserializer) getProcess();
    }
}
