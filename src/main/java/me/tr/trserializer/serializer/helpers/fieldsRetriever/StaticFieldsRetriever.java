package me.tr.trserializer.serializer.helpers.fieldsRetriever;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

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


    @Override
    public List<Field> getFields(Class<?> cls) {
        return new ArrayList<>(Arrays.asList(cls.getDeclaredFields()));
    }

    public List<Field> getFields(Class<?> cls, Predicate<Field> filter) {
        List<Field> fields = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            if (filter.test(field)) {
                fields.add(field);
            }
        }
        return fields;
    }
}
