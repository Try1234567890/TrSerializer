package me.tr.trserializer.deserializer.handlers.annotations.as;

import me.tr.trserializer.annotations.translator.cls.AsString;
import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.annotations.as.AsStringAnnotationSerializerHandler;
import me.tr.trserializer.utility.Wrappers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

public class AsStringAnnotationDeserializerHandler extends AsAnnotationDeserializerHandler<String> {
    public static final AsStringAnnotationDeserializerHandler INSTANCE = new AsStringAnnotationDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;
        Class<?> cls = task.getGenericType().getTypeClass();
        Field field = getField(cls, getAnnotation(task, AsString.class));

        setField(task, field, str);
    }

    private Field getField(Class<?> cls, AsString ann) {
        String fieldName = ann.field();
        try {
            return getField(cls, fieldName);
        } catch (HandlerError ex) {
            throw new HandlerError("No way to determinate which field use to deserialize @AsString class " + cls.getName() + ". Please specify a valid field name in @AsString annotation.", ex);
        }

    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Class<?> cls = task.getGenericType().getTypeClass();
        Annotation[] annotations = cls.getAnnotations();
        return Wrappers.isAssignable(task.getObjectClass(), String.class) && Arrays.stream(annotations).anyMatch(ann -> ann.annotationType().equals(AsString.class));
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return AsStringAnnotationSerializerHandler.INSTANCE;
    }

    @Override
    public Class<String> getAnnotationType() {
        return String.class;
    }
}
