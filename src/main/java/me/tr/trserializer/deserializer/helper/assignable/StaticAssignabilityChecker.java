package me.tr.trserializer.deserializer.helper.assignable;

import me.tr.trserializer.utility.Wrappers;

public class StaticAssignabilityChecker implements AssignabilityChecker {
    public static final StaticAssignabilityChecker INSTANCE = new StaticAssignabilityChecker();

    public static StaticAssignabilityChecker getInstance() {
        return INSTANCE;
    }

    public static boolean check(Class<?> cls, Class<?> other) {
        return INSTANCE.isAssignable(cls, other);
    }

    @Override
    public boolean isAssignable(Class<?> cls, Class<?> other) {
        if (cls == null || other == null) return false;
        // TODO: Adding options to allow only
        //       the field class to be assignable,
        //       excluding all children classes.


        return Wrappers.isAssignable(cls, other);
    }
}
