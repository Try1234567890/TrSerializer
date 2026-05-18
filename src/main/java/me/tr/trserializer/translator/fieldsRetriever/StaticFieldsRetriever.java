package me.tr.trserializer.translator.fieldsRetriever;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * The fields retriever is a system useful to retrieve and access
 * all declared fields in a class by following some defined rules.
 * <p>
 * This implementation does not contain any addition information
 * excluding the current field while processing. So the only
 * filter that can follow are @Annotation on fields.
 */
public class StaticFieldsRetriever implements FieldsRetriever {
    public static final StaticFieldsRetriever INSTANCE = new StaticFieldsRetriever();

    private StaticFieldsRetriever() {
    }


    public static StaticFieldsRetriever getInstance() {
        return INSTANCE;
    }

    public static List<Field> retrieve(Class<?> cls) {
        return INSTANCE.getFields(cls);
    }

    public static List<Field> retrieve(Class<?> cls, Predicate<Field> filter) {
        return INSTANCE.getFields(cls, filter);
    }


    /**
     * Retrieve all declared fields in the {@code class}.
     *
     * @param cls The class to work on.
     * @return The list of retrieved fields.
     */
    @Override
    public List<Field> getFields(Class<?> cls) {
        // TODO: Adding filter things like @Ingore.
        return getFields(cls, field -> true);
    }

    /**
     * Retrieve all declared fields that respects the {@code filter} (filter returns true) in the {@code class}.
     *
     * @param cls    The class to work on.
     * @param filter The filter to apply on each field.
     * @return The list of retrieved fields.
     */
    public List<Field> getFields(Class<?> cls, Predicate<Field> filter) {
        List<Field> fields = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            if (filter.test(field)) {
                field.setAccessible(true);
                fields.add(field);
            }
        }
        return fields;
    }
}
