package me.tr.trserializer.handlers.annotation;

import me.tr.trserializer.annotations.AsString;
import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.Process;
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

        Object newValue = getDeserializer().deserialize(obj, field.getType());
        Object instance = getProcess().getInstancer(Map.of("", newValue)).instance(clazz);

        try {
            getDeserializer().getValueSetter().setField(field, instance, newValue);

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("An error occurs while setting the value of field " + field.getName() + " in class " + Utility.getClassName(clazz), e);
        }
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        Class<?> clazz = obj.getClass();
        Field field = getField(clazz);

        try {
            field.setAccessible(true);
            Object value = field.get(obj);

            return getSerializer().serialize(value, String.class);
        } catch (Exception e) {
            throw new ProcessError("An error occurs while retrieving the value of field " + field.getName() + " in class " + Utility.getClassName(clazz), e);
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
                throw new ProcessError("The class " + Utility.getClassName(clazz) + " contains more than 1 field and no fields is specified " +
                        "in @AsString annotation. Please specify the field to working on in annotation param.");
            }

            for (Field field : fields) {
                if (paramName.equals(field.getName())) {
                    return field;
                }
            }

            throw new ProcessError("The field with name " + paramName + " is not found in class " + Utility.getClassName(clazz) + ". Make sure that the name is correct (case-sensitive).");
        }

        throw new ProcessError("Class " + Utility.getClassName(clazz) + " is not annotated with @AsString");
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
