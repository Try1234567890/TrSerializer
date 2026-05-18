package me.tr.trserializer.translator.fieldsRetriever;

import me.tr.trserializer.translator.FieldTask;
import me.tr.trserializer.translator.TranslatorTask;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * The fields retriever is a system useful to retrieve and access
 * all declared fields in a class by following some defined rules.
 * <p>
 * This implementation contains a {@link TranslatorTask} that can be used
 * to define more rules via its own translator or @Annotation on the definer class.
 */
public class TranslatorTaskFieldsRetriever implements FieldsRetriever {
    private final TranslatorTask task;

    public TranslatorTaskFieldsRetriever(TranslatorTask task) {
        this.task = task;
    }

    public TranslatorTask getTask() {
        return task;
    }

    /**
     * @return all declared fields in the type class of {@link FieldTask#getGenericType()} object.
     */
    public List<Field> getTypeFields() {
        Class<?> cls = getTask().getGenericType().getTypeClass();

        // TODO: Add task filters.

        return getFields(cls);
    }

    /**
     * @return all declared fields in the {@link FieldTask#getObject()} object.
     */
    public List<Field> getObjectFields() {
        Object object = getTask().getObject();
        if (object == null) return new ArrayList<>();

        // TODO: Add task filters.

        return getFields(object.getClass());
    }

    /**
     * Retrieve all declared fields in the {@code class}.
     *
     * @param cls The class to work on.
     * @return The list of retrieved fields.
     */
    @Override
    public List<Field> getFields(Class<?> cls) {
        return getFields(cls, (f) -> true);
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
