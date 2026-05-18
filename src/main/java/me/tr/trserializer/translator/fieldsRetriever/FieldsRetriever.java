package me.tr.trserializer.translator.fieldsRetriever;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * The fields retriever is a system useful to retrieve and access
 * all declared fields in a class by following some defined rules.
 */
public interface FieldsRetriever {

    /**
     * Retrieve the fields and access ({@link Field#setAccessible(boolean)}) from the {@code class}.
     *
     * @param cls The class to work on.
     * @return The list of all current class fields.
     */
    List<Field> getFields(Class<?> cls);

    /**
     * Retrieve the fields and access ({@link Field#setAccessible(boolean)}) from the
     * {@code class} and its super classes until {@link Object} class is reached (excluded).
     *
     * @param cls The class to work on.
     * @return The list of all current classes fields.
     */
    default List<Field> getFieldsWithSuperClasses(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = cls;

        while (!current.equals(Object.class)) {
            fields.addAll(getFields(current));
            current = current.getSuperclass();
        }

        return fields;
    }


}
