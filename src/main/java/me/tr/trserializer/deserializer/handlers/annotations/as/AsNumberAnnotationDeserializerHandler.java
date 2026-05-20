package me.tr.trserializer.deserializer.handlers.annotations.as;

import me.tr.trserializer.annotations.translator.cls.AsNumber;
import me.tr.trserializer.annotations.translator.cls.AsString;
import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.annotations.as.AsNumberAnnotationSerializerHandler;
import me.tr.trserializer.serializer.handlers.annotations.as.AsStringAnnotationSerializerHandler;
import me.tr.trserializer.utility.Wrappers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

public class AsNumberAnnotationDeserializerHandler extends AsAnnotationDeserializerHandler<Number> {
    public static final AsNumberAnnotationDeserializerHandler INSTANCE = new AsNumberAnnotationDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof Number num)) return;
        Class<?> cls = task.getGenericType().getTypeClass();
        Field field = getField(cls, getAnnotation(task, AsNumber.class));

        setField(task, field, num);
    }

    private Field getField(Class<?> cls, AsNumber ann) {
        String fieldName = ann.field();
        try {
            return getField(cls, fieldName);
        } catch (HandlerError ex) {
            throw new HandlerError("No way to determinate which field use to deserialize @AsNumber class " + cls.getName() + ". Please specify a valid field name in @AsNumber annotation.", ex);
        }

    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Class<?> cls = task.getGenericType().getTypeClass();
        Annotation[] annotations = cls.getAnnotations();
        return Wrappers.isAssignable(task.getObjectClass(), Number.class) && Arrays.stream(annotations).anyMatch(ann -> ann.annotationType().equals(AsNumber.class));
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return AsNumberAnnotationSerializerHandler.INSTANCE;
    }

    @Override
    public Class<Number> getAnnotationType() {
        return Number.class;
    }
}
