package me.tr.trserializer.processes.serializer;

import me.tr.trserializer.processes.process.ProcessOptions;
import me.tr.trserializer.processes.options.Option;
import me.tr.trserializer.processes.options.Options;
import me.tr.trserializer.utility.Three;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializerOptions extends ProcessOptions {
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
        if (fieldName == null) {
            getSerializer().getLogger().warn("The field name to add aliases  is null");
            return this;
        }
        if (aliases == null) {
            getSerializer().getLogger().warn("The aliases to add is null");
            return this;
        }
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
        if (alias == null) {
            getSerializer().getLogger().warn("The aliases to add is null");
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
     */
    public boolean hasAliases(Class<?> clazz) {
        if (clazz == null) {
            getSerializer().getLogger().throwable(new NullPointerException("The class is null."));
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
        if (clazz == null) {
            getSerializer().getLogger().throwable(new NullPointerException("The class is null."));
            return false;
        }
        if (fieldName == null) {
            getSerializer().getLogger().throwable(new NullPointerException("The field name is null."));
            return false;
        }
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
            getSerializer().getLogger().throwable(new NullPointerException("The field is null."));
            return false;
        }

        return hasAliases(field.getDeclaringClass(), field.getName());
    }

    public Serializer getSerializer() {
        return (Serializer) getProcess();
    }
}
