package me.tr.serializer.handlers;

import me.tr.serializer.annotations.AsNumber;
import me.tr.serializer.annotations.AsString;
import me.tr.serializer.converters.Converter;
import me.tr.serializer.expections.TypeMissMatched;
import me.tr.serializer.instancers.ProcessInstancer;
import me.tr.serializer.processes.Process;
import me.tr.serializer.registries.ConvertersRegistry;
import me.tr.serializer.types.GenericType;
import me.tr.serializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.List;


public class AsNumberHandler extends AsStringHandler {


    public AsNumberHandler(Process process) {
        super(process);
    }

    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        Class<?> clazz = type.getClazz();

        AsNumber annotation = clazz.getAnnotation(AsNumber.class);
        String paramName = annotation.param();
        Class<?> resultType = annotation.type();
        ProcessInstancer instancer = getProcess().getInstancer();
        Object instance = instancer.instance(clazz);

        try {
            Field field = getField(paramName, clazz);

            if (field != null) {
                Class<?> fieldType = resultType == Number.class ? field.getType() : resultType;
                Object value = getProcess().process(obj, fieldType);

                if (value instanceof Number num
                        && !fieldType.isAssignableFrom(value.getClass())) {
                    Converter<Number, ?> converter = ConvertersRegistry.getConverter(Number.class, resultType);
                    if (converter != null)
                        value = converter.primitive(num);
                }

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
        AsNumber annotation = obj.getClass().getAnnotation(AsNumber.class);
        String paramName = annotation.param();
        Class<?> resultType = annotation.type();
        Class<?> clazz = obj.getClass();

        try {
            Field field = getField(paramName, clazz);
            if (field != null) {
                field.setAccessible(true);
                Object value = getProcess().process(field.get(obj), type);

                if (value instanceof Number num && !resultType.isAssignableFrom(value.getClass())) {
                    Converter<Number, ?> converter = ConvertersRegistry.getConverter(Number.class, resultType);
                    if (converter != null)
                        value = converter.primitive(num);
                }

                return value;
            } else
                throw new NoSuchFieldException("Specify a valid field to retrieve the value from in @AsString annotation in " + clazz);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("No fields found with name: " + paramName + " in " + clazz, e);
        }
    }
}
