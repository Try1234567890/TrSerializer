package me.tr.trserializer.processes.deserializer.helper;

import me.tr.trformatter.strings.CString;
import me.tr.trserializer.annotations.Essential;
import me.tr.trserializer.annotations.Setter;
import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

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
            return;
        }

        try {
            if (field.isAnnotationPresent(Essential.class)
                    && !getDeserializer().getProcessValidator().isValid(value).isSuccess()) {
                throw new ProcessError("The value for field " + fieldName + " in class " + className + " hasn't pass the validation and the field is annotated with @Essential.");
            }

            if (setFieldWithAnnotation(field, instance, value))
                return;

            field.set(instance, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ProcessError("An error occurs while setting the value to " + fieldName + " in class " + className, e);
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
                    "set" + CString.of(fieldName).toPascalCase(),
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
            throw new ProcessError("No methods found in class " + className + " with names " + Arrays.toString(methodNames));
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
