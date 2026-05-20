package me.tr.trserializer.translator.fieldsRetriever;

import me.tr.trserializer.annotations.filter.*;
import me.tr.trserializer.utility.SLogger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * The fields retriever is a system useful to retrieve and access
 * all declared fields in a class by following some defined rules.
 * <p>
 * This implementation does not contain any addition information
 * excluding the current field while processing. So the only
 * filter that can follow are @Annotation on fields.
 */
public class StaticFieldsRetriever implements FieldsRetriever {
    public static final StaticFieldsRetriever INSTANCE = new StaticFieldsRetriever();

    private StaticFieldsRetriever() {
    }


    public static StaticFieldsRetriever getInstance() {
        return INSTANCE;
    }

    public static List<Field> retrieve(Class<?> cls) {
        return INSTANCE.getFields(cls);
    }

    public static List<Field> retrieve(Class<?> cls, Predicate<Field> filter) {
        return INSTANCE.getFields(cls, filter);
    }


    /**
     * Retrieve all declared fields in the {@code class}.
     *
     * @param cls The class to work on.
     * @return The list of retrieved fields.
     */
    @Override
    public List<Field> getFields(Class<?> cls) {
        return getFields(cls, f -> !isIgnored(f));
    }

    /**
     * Retrieve all declared fields that respects the {@code filter} (filter returns true) in the {@code class}.
     *
     * @param cls    The class to work on.
     * @param filter The filter to apply on each field.
     * @return The list of retrieved fields.
     */
    public List<Field> getFields(Class<?> cls, Predicate<Field> filter) {
        SLogger.LOGGER.debug("Retrieve fields for " + cls.getName());
        List<Field> fields = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            if (filter.test(field)) {
                field.setAccessible(true);
                fields.add(field);
                SLogger.LOGGER.debug("  Field " + field.getName() + " is in " + cls.getName() + " has been included.");
            }
            SLogger.LOGGER.debug("  Field " + field.getName() + " is in " + cls.getName() + " has been ignored.");
        }
        return fields;
    }

    private boolean isIgnored(Field field) {
        if (field.isAnnotationPresent(Ignore.class)) {
            SLogger.LOGGER.debug("Field " + field.getName() + " is ignored because is annotated with @Ignore.");
            return true;
        }
        Class<?> cls = field.getDeclaringClass();

        if (cls.isAnnotationPresent(Ignore.class)) {
            SLogger.LOGGER.debug("Field " + field.getName() + " is ignored because " + cls.getName() + "  is annotated with @Ignore.");
            return true;
        }

        if (cls.isAnnotationPresent(IgnoreFinal.class) && Modifier.isFinal(field.getModifiers())) {
            SLogger.LOGGER.debug("Field " + field.getName() + " is ignored because is Final and " + cls.getName() + " is annotated @IgnoreFinalwith and  ");
            return true;
        }
        if (cls.isAnnotationPresent(IgnoreStatic.class) && Modifier.isStatic(field.getModifiers())) {
            SLogger.LOGGER.debug("Field " + field.getName() + " is ignored because is Static and " + cls.getName() + " is annotated with @IgnoreStatic and ");
            return true;
        }
        if (cls.isAnnotationPresent(IgnoreTransient.class) && Modifier.isTransient(field.getModifiers())) {
            SLogger.LOGGER.debug("Field " + field.getName() + " is ignored because is Transient and " + cls.getName() + " is annotated with @IgnoreTransient and ");
            return true;
        }
        if (cls.isAnnotationPresent(IgnoreVolatile.class) && Modifier.isVolatile(field.getModifiers())) {
            SLogger.LOGGER.debug("Field " + field.getName() + " is ignored because is Volatile and " + cls.getName() + " is annotated with @IgnoreVolatile and ");
            return true;
        }
        if (cls.isAnnotationPresent(IgnoreProtected.class) && Modifier.isProtected(field.getModifiers())) {
            SLogger.LOGGER.debug("Field " + field.getName() + " is ignored because is Protected and " + cls.getName() + " is annotated with @IgnoreProtected and ");
            return true;
        }
        if (cls.isAnnotationPresent(IgnorePrivate.class) && Modifier.isPrivate(field.getModifiers())) {
            SLogger.LOGGER.debug("Field " + field.getName() + " is ignored because is Private and " + cls.getName() + " is annotated with @IgnorePrivate and ");
            return true;
        }
        if (cls.isAnnotationPresent(IgnorePublic.class) && Modifier.isPublic(field.getModifiers())) {
            SLogger.LOGGER.debug("Field " + field.getName() + " is ignored because is Public and " + cls.getName() + " is annotated with @IgnorePublic and ");
            return true;
        }

        return false;
    }
}
