package me.tr.trserializer.serializer.helpers.fieldsRetriever;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface FieldsRetriever {

    List<Field> getFields(Class<?> cls);

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
