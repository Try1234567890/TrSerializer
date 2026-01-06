package me.tr.trserializer.handlers.annotation;

import me.tr.trserializer.annotations.AsBoolean;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.instancers.ProcessInstancer;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Map;
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
            TrLogger.exception(new RuntimeException("An error occurs while retrieving the value of field " + field.getName() + " in class " + clazz.getName(), e));
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
                TrLogger.exception(
                        new NullPointerException("The class " + clazz.getName() + " contains more than 1 field and no fields is specified " +
                                "in @AsBoolean annotation. Please specify the field to working on in annotation param."));
                return null;
            }

            for (Field field : fields) {
                if (paramName.equals(field.getName())) {
                    return field;
                }
            }

            TrLogger.exception(new NullPointerException("The field with name " + paramName + " is not found in class " + clazz.getName() + ". Make sure that the name is correct (case-sensitive)."));
        }

        return null;
    }
}
