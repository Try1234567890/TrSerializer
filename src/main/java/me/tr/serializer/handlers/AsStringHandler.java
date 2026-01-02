package me.tr.serializer.handlers;

import me.tr.serializer.annotations.AsString;
import me.tr.serializer.exceptions.TypeMissMatched;
import me.tr.serializer.instancers.ProcessInstancer;
import me.tr.serializer.processes.deserializer.Deserializer;
import me.tr.serializer.processes.serializer.Serializer;
import me.tr.serializer.types.GenericType;
import me.tr.serializer.processes.Process;

import java.lang.reflect.Field;
import java.util.List;


public class AsStringHandler implements TypeHandler {
    private final Process process;

    public AsStringHandler(Process process) {
        this.process = process;
    }

    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        Class<?> clazz = type.getClazz();
        AsString annotation = clazz.getAnnotation(AsString.class);
        String paramName = annotation.param();
        ProcessInstancer instancer = getProcess().getInstancer();
        Object instance = instancer.instance(clazz);

        try {
            Field field = getField(paramName, clazz);
            if (field != null) {
                Class<?> fieldType = field.getType();
                Object value = getDeserializer().deserialize(obj, fieldType);

                if (value != null && !fieldType.equals(value.getClass()))
                    throw new TypeMissMatched("The provided object type " + value.getClass() + " is not the expected one: " + fieldType);

                field.setAccessible(true);
                field.set(instance, value);
            } else
                throw new NoSuchFieldException("Specify a valid field to assign the deserialized value to in @AsString annotation in " + clazz);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("No fields found with name: " + paramName + " in " + clazz, e);
        }
        return instance;
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        AsString annotation = obj.getClass().getAnnotation(AsString.class);
        String paramName = annotation.param();
        Class<?> clazz = obj.getClass();

        try {
            Field field = getField(paramName, clazz);
            if (field != null) {
                field.setAccessible(true);
                Object value = getSerializer().serialize(field.get(obj));
                return value instanceof String str ? str : String.valueOf(value);
            } else
                throw new NoSuchFieldException("Specify a valid field to retrieve the value from in @AsString annotation in " + clazz);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("No fields found with name: " + paramName + " in " + clazz, e);
        }
    }

    protected Field getField(String paramName, Class<?> clazz) throws NoSuchFieldException {
        if (paramName != null && !paramName.isEmpty()) {
            return clazz.getDeclaredField(paramName);
        } else {
            List<Field> fields = getProcess().getFields(clazz);
            if (fields.size() == 1)
                return fields.getFirst();
        }
        return null;
    }

    public Process getProcess() {
        return process;
    }


    protected Deserializer getDeserializer() {
        return (Deserializer) process;
    }

    protected Serializer getSerializer() {
        return (Serializer) process;
    }
}
