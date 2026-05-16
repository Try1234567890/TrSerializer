package me.tr.trserializer.serializer.helpers.savable;

import me.tr.trserializer.serializer.SerializerTask;

public final class SerializerTaskSavabilityChecker implements SavabilityChecker {
    private final SerializerTask task;

    public SerializerTaskSavabilityChecker(SerializerTask task) {
        if (task == null) throw new IllegalArgumentException("The task cannot be null.");
        this.task = task;
    }

    public SerializerTask getTask() {
        return task;
    }

    public boolean isSavable() {
        return isSavable(getTask().getObject());
    }

    @Override
    public boolean isSavable(Object object) {
        return StaticSavabilityChecker.check(object);
    }
}
