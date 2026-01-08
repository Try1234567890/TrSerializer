package me.tr.trserializer.processes.deserializer.helper;

import me.tr.trformatter.strings.format.formats.CamelCase;
import me.tr.trformatter.strings.format.formats.PascalCase;
import me.tr.trserializer.annotations.Essential;
import me.tr.trserializer.annotations.Setter;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DValueSetter {
    private final Deserializer deserializer;


    public DValueSetter(Deserializer deserializer) {
        this.deserializer = deserializer;
    }

    /**
     * Set the field value inside the provided instance.
     *
     * @param field    The field to set value to.
     * @param instance The instance to set field to.
     * @param value    The value to set.
     */
    public void setField(Field field, Object instance, Object value) {

        String fieldName = field.getName();
        Class<?> instanceClass = instance.getClass();
        String className = Utility.getClassName(instanceClass);

        if (!hasFields(instanceClass)) {
            getDeserializer().getLogger().debug("Class " + className + " does not have fields. Stopping setting value for " + fieldName);
            return;
        }

        try {
            if (field.isAnnotationPresent(Essential.class)
                    && !getDeserializer().getProcessValidator().isValid(value).isSuccess()) {
                getDeserializer().getLogger().throwable(new NullPointerException("The value for field " + fieldName + " in class " + className + " hasn't pass the validation and the field is annotated with @Essential."));
                return;
            }

            if (setFieldWithAnnotation(field, instance, value))
                return;

            field.set(instance, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            getDeserializer().getLogger().throwable(new RuntimeException("An error occurs while setting the value to " + fieldName + " in class " + className, e));
        }
    }

    /**
     * Set the field value inside the provided instance.
     *
     * @param field    The field to set value to.
     * @param instance The instance to set field to.
     * @param value    The value to set.
     * @return {@code true} if the value has been set, otherwise {@code false}.
     * @throws InvocationTargetException if an error occurs.
     * @throws IllegalAccessException    if an error occurs.
     */
    private boolean setFieldWithAnnotation(Field field, Object instance, Object value)
            throws InvocationTargetException, IllegalAccessException {
        String fieldName = field.getName();
        Class<?> instanceClass = instance.getClass();
        String className = Utility.getClassName(instanceClass);

        if (field.isAnnotationPresent(Setter.class)) {
            Setter ann = field.getAnnotation(Setter.class);

            String[] methodNames = {
                    ann.name(),
                    "set" + new PascalCase(fieldName).toCaseFrom(CamelCase.class).getResult(),
                    fieldName
            };

            for (String methodName : methodNames) {
                if (methodName == null || methodName.isEmpty()) continue;
                try {
                    Method setter = instanceClass.getDeclaredMethod(methodName, field.getType());
                    setter.setAccessible(true);
                    setter.invoke(instance, value);
                    return true;
                } catch (NoSuchMethodException ignored) {
                }
            }
            getDeserializer().getLogger().throwable(new NoSuchMethodException("No methods found in class " + className + " with names " + Arrays.toString(methodNames) + ". Setting value to the field directly..."));
        }

        return false;
    }

    private boolean hasFields(Class<?> clazz) {
        return !clazz.isEnum() && !clazz.isPrimitive() && !clazz.isRecord();
    }

    public Deserializer getDeserializer() {
        return deserializer;
    }
}
