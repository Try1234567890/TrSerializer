package me.tr.trserializer.deserializer.helper.assignable;

public class ObjectAssignabilityChecker implements AssignabilityChecker {
    private final Object object;
    private final Class<?> cls;

    public ObjectAssignabilityChecker(Object object) {
        this.object = object;
        this.cls = object == null ? Object.class : object.getClass();
    }

    public Object getObject() {
        return object;
    }

    public Class<?> getCls() {
        return cls;
    }

    public boolean isAssignable(Class<?> other) {
        return isAssignable(getCls(), other);
    }


    @Override
    public boolean isAssignable(Class<?> cls, Class<?> other) {
        return StaticAssignabilityChecker.check(cls, other);
    }
}
