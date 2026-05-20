package me.tr.trserializer.deserializer.handlers.annotations.as;

import me.tr.trserializer.annotations.translator.cls.AsBoolean;
import me.tr.trserializer.annotations.translator.cls.AsString;
import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.annotations.as.AsBooleanAnnotationSerializerHandler;
import me.tr.trserializer.serializer.handlers.annotations.as.AsStringAnnotationSerializerHandler;
import me.tr.trserializer.utility.Wrappers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

public class AsBooleanAnnotationDeserializerHandler extends AsAnnotationDeserializerHandler<Boolean> {
    public static final AsBooleanAnnotationDeserializerHandler INSTANCE = new AsBooleanAnnotationDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof Boolean bool)) return;
        Class<?> cls = task.getGenericType().getTypeClass();
        Field field = getField(cls, getAnnotation(task, AsBoolean.class));

        setField(task, field, bool);
    }

    private Field getField(Class<?> cls, AsBoolean ann) {
        String fieldName = ann.field();
        try {
            return getField(cls, fieldName);
        } catch (HandlerError ex) {
            throw new HandlerError("No way to determinate which field use to deserialize @AsBoolean class " + cls.getName() + ". Please specify a valid field name in @AsBoolean annotation.", ex);
        }

    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Class<?> cls = task.getGenericType().getTypeClass();
        Annotation[] annotations = cls.getAnnotations();
        return Wrappers.isAssignable(task.getObjectClass(), Boolean.class) && Arrays.stream(annotations).anyMatch(ann -> ann.annotationType().equals(AsBoolean.class));
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return AsBooleanAnnotationSerializerHandler.INSTANCE;
    }

    @Override
    public Class<Boolean> getAnnotationType() {
        return Boolean.class;
    }
}
