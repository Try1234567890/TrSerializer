package me.tr.trserializer.serializer.helpers.savable;

public final class ObjectSavabilityChecker implements SavabilityChecker {
    private final Object object;

    public ObjectSavabilityChecker(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public boolean isSavable() {
        return isSavable(getObject());
    }

    @Override
    public boolean isSavable(Object object) {
        return StaticSavabilityChecker.check(object);
    }
}
