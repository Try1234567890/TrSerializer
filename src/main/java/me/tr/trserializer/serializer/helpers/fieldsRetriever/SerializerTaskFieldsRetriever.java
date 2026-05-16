package me.tr.trserializer.serializer.helpers.fieldsRetriever;

import me.tr.trserializer.serializer.SerializerTask;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SerializerTaskFieldsRetriever implements FieldsRetriever {
    private final SerializerTask task;

    public SerializerTaskFieldsRetriever(SerializerTask task) {
        this.task = task;
    }

    public SerializerTask getTask() {
        return task;
    }

    public List<Field> getFields() {
        Object object = getTask().getObject();
        if (object == null) return new ArrayList<>();

        // TODO: Add task filters.

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
