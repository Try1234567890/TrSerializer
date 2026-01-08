package me.tr.trserializer.handlers.annotation;

import me.tr.trserializer.annotations.AsString;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.instancers.ProcessInstancer;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class AsStringHandler implements TypeHandler {
    private final Process process;

    public AsStringHandler(Process process) {
        this.process = process;
    }

    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        Class<?> clazz = type.getTypeClass();
        Field field = getField(clazz);

        if (field == null)
            return null;

        Object newValue = getDeserializer().deserialize(obj, field.getType());

        if (clazz.isRecord()) {
            return new ProcessInstancer(getProcess(), Map.of("", newValue)).instance(clazz);
        }

        Object instance = getProcess().instance(clazz);

        try {
            field.setAccessible(true);

            field.set(instance, newValue);

            return instance;
        } catch (Exception e) {
            TrLogger.exception(new RuntimeException("An error occurs while setting the value of field " + field.getName() + " in class " + Utility.getClassName(clazz), e));
            return null;
        }
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

            return getSerializer().serialize(value, String.class);
        } catch (Exception e) {
            TrLogger.exception(new RuntimeException("An error occurs while retrieving the value of field " + field.getName() + " in class " + Utility.getClassName(clazz), e));
            return null;
        }
    }

    private Field getField(Class<?> clazz) {
        if (clazz.isAnnotationPresent(AsString.class)) {
            Set<Field> fields = getProcess().getFields(clazz);

            if (fields.size() == 1)
                return fields.iterator().next();

            AsString asString = clazz.getAnnotation(AsString.class);
            String paramName = asString.field();

            if (paramName.isEmpty()) {
                TrLogger.exception(
                        new NullPointerException("The class " + Utility.getClassName(clazz) + " contains more than 1 field and no fields is specified " +
                                "in @AsString annotation. Please specify the field to working on in annotation param."));
                return null;
            }

            for (Field field : fields) {
                if (paramName.equals(field.getName())) {
                    return field;
                }
            }

            TrLogger.exception(new NullPointerException("The field with name " + paramName + " is not found in class " + Utility.getClassName(clazz) + ". Make sure that the name is correct (case-sensitive)."));
        }

        return null;
    }

    public Process getProcess() {
        return process;
    }

    protected Deserializer getDeserializer() {
        return (Deserializer) getProcess();
    }

    protected Serializer getSerializer() {
        return (Serializer) getProcess();
    }
}
