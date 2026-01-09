package me.tr.trserializer.processes.process.addons;

import me.tr.trserializer.annotations.unwrap.Unwrap;
import me.tr.trserializer.annotations.unwrap.Unwrapped;
import me.tr.trserializer.processes.process.Process;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class providing shared logic for {@link PAddon} implementations.
 * <p>
 * This class centralizes common operations that are required by multiple addons
 * which do not share a specific inheritance hierarchy.
 * </p>
 */
public class PAddonsUtility {

    /**
     * Identifies and retrieves the set of fields to be unwrapped from the given instance.
     * <p>
     * A field is included in the result if it meets at least one of the following criteria:
     * <ul>
     * <li>It is explicitly annotated with {@link Unwrap}.</li>
     * <li>Its name is specified in the {@code fields} property of the {@link Unwrapped} annotation.</li>
     * </ul>
     * Only fields considered valid by the current {@link Process} (e.g., not ignored or filtered)
     * are evaluated.
     * </p>
     *
     * @param process   the current serialization or deserialization process used for field discovery.
     * @param unwrapped the annotation containing the list of target field names.
     * @param instance  the object instance from which to extract the fields.
     * @return a {@link Set} of {@link Field} objects eligible for unwrapping.
     */
    public static Set<Field> getFields(Process process, Unwrapped unwrapped, Object instance) {
        Set<Field> fields = new HashSet<>();
        Set<String> fieldNamesFromAnn = Arrays.stream(unwrapped.fields()).collect(Collectors.toSet());

        for (Field field : process.getFields(instance.getClass())) {
            String fieldName = field.getName();

            if (field.isAnnotationPresent(Unwrap.class) ||
                    fieldNamesFromAnn.contains(fieldName)) {
                fields.add(field);
            }
        }

        return fields;
    }

}