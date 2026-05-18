package me.tr.trserializer.deserializer.helper.assignable;

import me.tr.trserializer.deserializer.DeserializerFieldTask;

import java.lang.reflect.Field;

public class DeserializerFieldTaskAssignabilityChecker implements AssignabilityChecker {
    private final DeserializerFieldTask task;

    public DeserializerFieldTaskAssignabilityChecker(DeserializerFieldTask task) {
        this.task = task;
    }

    public DeserializerFieldTask getTask() {
        return task;
    }

    public boolean isAssignable() {
        return isAssignable(getTask().getField());
    }

    public boolean isAssignable(Field field) {
        // TODO: Add task options.
        Object object = getTask().getObject();

        if (object == null) {
            // TODO: Adding options to allow null value.
            return false;
        }

        return isAssignable(object.getClass(), field.getType());
    }

    @Override
    public boolean isAssignable(Class<?> cls, Class<?> other) {
        // TODO: Add task options.

        return StaticAssignabilityChecker.check(cls, other);
    }
}
