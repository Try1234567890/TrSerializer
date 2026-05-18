package me.tr.trserializer.deserializer.helper.assignable;

import me.tr.trserializer.deserializer.DeserializerTask;

public class DeserializerTaskAssignabilityChecker implements AssignabilityChecker {
    private final DeserializerTask task;

    public DeserializerTaskAssignabilityChecker(DeserializerTask task) {
        this.task = task;
    }

    public DeserializerTask getTask() {
        return task;
    }

    public boolean isAssignable() {
        Object object = getTask().getObject();

        if (object == null) {
            // TODO: Adding options to allow null value.
            return false;
        }

        return isAssignable(object.getClass(), getTask().getGenericType().getTypeClass());
    }

    public boolean isAssignable(Class<?> other) {
        Object object = getTask().getObject();

        if (object == null) {
            // TODO: Adding options to allow null value.
            return false;
        }

        return isAssignable(object.getClass(), other);
    }

    @Override
    public boolean isAssignable(Class<?> cls, Class<?> other) {
        // TODO: Add task options.

        return StaticAssignabilityChecker.check(cls, other);
    }
}
