package me.tr.trserializer.translator.fieldsRetriever;

import me.tr.trserializer.utility.SLogger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * The fields retriever is a system useful to retrieve and access
 * all declared fields in a class by following some defined rules.
 * <p>
 * This implementation is designed mainly to handle fields retrieving
 * of the same object multiple times, like a cache entry.
 * This implementation retrieve fields one time and insert them
 * into a list. For the next times that the fields is requested
 * the stored list is provided.
 */
public class ObjectFieldsRetriever implements FieldsRetriever {
    private final Object object;
    private final List<Field> fields;

    public ObjectFieldsRetriever(Object object) {
        this.object = object;
        fields = new ArrayList<>();
    }

    public Object getObject() {
        return object;
    }

    /**
     * Retrieve all declared fields in the {@code object} provided in constructor.
     *
     * @return The list of retrieved fields.
     */
    public List<Field> getFields() {
        if (!fields.isEmpty()) {
            SLogger.LOGGER.debug("Fields already retrieved, returning them.");
            return fields;
        }

        Object object = getObject();
        if (object == null) {
            SLogger.LOGGER.error("Object is null, cannot retrieve fields. Returning empty list.");
            return new ArrayList<>();
        }

        List<Field> result = getFields(object.getClass());
        fields.addAll(result);
        return result;
    }

    /**
     * Retrieve all declared fields in the {@code class}.
     *
     * @param cls The class to work on.
     * @return The list of retrieved fields.
     */
    @Override
    public List<Field> getFields(Class<?> cls) {
        return StaticFieldsRetriever.retrieve(cls);
    }

    /**
     * Retrieve all declared fields that respects the {@code filter} (filter returns true) in the {@code class}.
     *
     * @param cls    The class to work on.
     * @param filter The filter to apply on each field.
     * @return The list of retrieved fields.
     */
    public List<Field> getFields(Class<?> cls, Predicate<Field> filter) {
        return StaticFieldsRetriever.retrieve(cls, filter);
    }
}
