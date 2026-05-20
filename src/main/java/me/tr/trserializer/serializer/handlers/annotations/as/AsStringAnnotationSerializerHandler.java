package me.tr.trserializer.serializer.handlers.annotations.as;

import me.tr.trserializer.annotations.translator.cls.AsString;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.annotations.as.AsStringAnnotationDeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.types.GenericType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

public class AsStringAnnotationSerializerHandler extends AsAnnotationSerializerHandler<String> {
    public static final AsStringAnnotationSerializerHandler INSTANCE = new AsStringAnnotationSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        Object instance = task.getObject();
        Class<?> cls = instance.getClass();
        Field field = getField(cls, getAnnotation(task, AsString.class));

        save(task, field, instance, GenericType.STRING);
    }

    private Field getField(Class<?> cls, AsString ann) {
        String fieldName = ann.field();
        try {
            return getField(cls, fieldName);
        } catch (HandlerError ex) {
            throw new HandlerError("No way to determinate which field use to serialize @AsString class " + cls.getName() + ". Please specify a valid field name in @AsString annotation.", ex);
        }
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object object = task.getObject();
        if (object == null) return false;
        Annotation[] annotations = object.getClass().getAnnotations();
        return Arrays.stream(annotations).anyMatch(ann -> ann.annotationType().equals(AsString.class));
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return AsStringAnnotationDeserializerHandler.INSTANCE;
    }

    @Override
    public Class<String> getAnnotationType() {
        return String.class;
    }
}
