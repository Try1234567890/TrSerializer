package me.tr.trserializer.handlers.annotation;

import me.tr.trserializer.annotations.AsBoolean;
import me.tr.trserializer.logger.ProcessLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.Set;

public class AsBooleanHandler extends AsStringHandler {

    public AsBooleanHandler(Process process) {
        super(process);
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        Class<?> clazz = obj.getClass();
        Field field = getField(clazz);

        if (field == null)
            return null;

        try {
            field.setAccessible(true);
            Object value = field.get(obj);

            return getSerializer().serialize(value, Boolean.class);
        } catch (Exception e) {
            getProcess().getLogger().throwable(new RuntimeException("An error occurs while retrieving the value of field " + field.getName() + " in class " + Utility.getClassName(clazz), e));
            return null;
        }
    }

    private Field getField(Class<?> clazz) {
        if (clazz.isAnnotationPresent(AsBoolean.class)) {
            Set<Field> fields = getProcess().getFields(clazz);

            if (fields.size() == 1)
                return fields.iterator().next();

            AsBoolean asString = clazz.getAnnotation(AsBoolean.class);
            String paramName = asString.field();

            if (paramName.isEmpty()) {
                getProcess().getLogger().throwable(
                        new NullPointerException("The class " + Utility.getClassName(clazz) + " contains more than 1 field and no fields is specified " +
                                "in @AsBoolean annotation. Please specify the field to working on in annotation param."));
                return null;
            }

            for (Field field : fields) {
                if (paramName.equals(field.getName())) {
                    return field;
                }
            }

            getProcess().getLogger().throwable(new NullPointerException("The field with name " + paramName + " is not found in class " + Utility.getClassName(clazz) + ". Make sure that the name is correct (case-sensitive)."));
        }

        return null;
    }
}
