package me.tr.serializer.processes.serializer;

import me.tr.serializer.logger.TrLogger;
import me.tr.serializer.processes.ProcessOptions;
import me.tr.serializer.processes.options.Option;
import me.tr.serializer.processes.options.Options;
import me.tr.serializer.utility.Three;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializerOptions extends ProcessOptions {
    private final Option<Map<Class<?>, String[]>> endMethods = new Option<>(Options.SER_END_METHODS, new HashMap<>());
    private final Option<List<Three<Class<?>, String, String>>> aliases = new Option<>(Options.MAP_ALIASES, new ArrayList<>());


    public SerializerOptions(Serializer process) {
        super(process);
    }

    /*
     * =------------=
     * ALIASES OPTION
     * =------------=
     */

    public Option<List<Three<Class<?>, String, String>>> getAliasesOption() {
        return aliases;
    }

    /**
     * Retrieve all aliases registered in this deserializer.
     *
     * @return all known aliases.
     */
    public List<Three<Class<?>, String, String>> getAliases() {
        return getAliasesOption().getValue();
    }

    /**
     * Add a new alias to this deserializer.
     *
     * @param declaringClazz The class that contains the field. (all classes if {@code null})
     * @param fieldName      The field name to register alias to.
     * @param aliases        The aliases to register.
     */
    public SerializerOptions addAlias(Class<?> declaringClazz, String fieldName, String aliases) {
        getAliasesOption().getValue().add(new Three<>(declaringClazz, fieldName, aliases));
        return this;
    }

    /**
     * Add a new alias to this deserializer.
     *
     * @param alias A tree object containing all information for alias.
     * @see #addAlias(Class, String, String)
     */
    public SerializerOptions addAlias(Three<Class<?>, String, String> alias) {
        getAliasesOption().getValue().add(alias);
        return this;
    }

    /**
     * Checks if the class has aliases.
     *
     * @param clazz The class to check for.
     * @return {@code true} if it has, otherwise {@code false}.
     */
    public boolean hasAliases(Class<?> clazz) {
        if (clazz == null) {
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
     */
    public boolean hasAliases(Class<?> clazz, String fieldName) {
        return hasAliases(clazz) && getAliases().stream().anyMatch(alias -> alias.value().equalsIgnoreCase(fieldName));
    }

    /**
     * Checks if the field has aliases.
     *
     * @param field The field name to check for.
     * @return {@code true} if it has, otherwise {@code false}.
     */
    public boolean hasAliases(Field field) {
        if (field == null) {
            return false;
        }

        return hasAliases(field.getDeclaringClass(), field.getName());
    }


    /*
     * =----------------=
     * END METHODS OPTION
     * =----------------=
     */
    public Option<Map<Class<?>, String[]>> getEndMethodsOption() {
        return endMethods;
    }

    public Map<Class<?>, String[]> getEndMethods() {
        return getEndMethodsOption().getValue();
    }

    public boolean hasEndMethods(Class<?> clazz) {
        if (clazz == null)
            return false;

        return getEndMethods().containsKey(clazz);
    }

    public String[] getEndMethodNamesFor(Class<?> clazz) {
        if (clazz == null) {
            TrLogger.getInstance().exception(
                    new NullPointerException("The class is null."));
            return new String[0];
        }

        return getEndMethods().get(clazz);
    }

    public Serializer getSerializer() {
        return (Serializer) getProcess();
    }
}
