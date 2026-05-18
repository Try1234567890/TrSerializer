package me.tr.trserializer.deserializer.helper.assignable;

public interface AssignabilityChecker {

    boolean isAssignable(Class<?> cls, Class<?> other);

}
