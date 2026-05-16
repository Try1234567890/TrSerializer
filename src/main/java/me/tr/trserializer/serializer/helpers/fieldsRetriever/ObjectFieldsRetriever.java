package me.tr.trserializer.serializer.helpers.fieldsRetriever;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ObjectFieldsRetriever implements FieldsRetriever {
    private final Object object;

    public ObjectFieldsRetriever(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public List<Field> getFields() {
        Object object = getObject();
        if (object == null) return new ArrayList<>();

        return getFields(object.getClass());
    }

    @Override
    public List<Field> getFields(Class<?> cls) {
        return StaticFieldsRetriever.retrieve(cls);
    }

    public List<Field> getFields(Class<?> cls, Predicate<Field> filter) {
        return StaticFieldsRetriever.retrieve(cls, filter);
    }
}
