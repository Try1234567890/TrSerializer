package me.tr.trserializer.serializer.handlers.annotations.as;

import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.SLogger;
import me.tr.trserializer.utility.Wrappers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public abstract class AsAnnotationSerializerHandler<T> implements SerializerHandler {
    private final Collection<BiFunction<Class<?>, String, Field>> FUNCTIONS = List.of(
            (cls, fieldName) -> {
                try {
                    return cls.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    return null;
                }
            },
            (cls, fieldName) -> {
                Field[] fields = cls.getDeclaredFields();
                if (fields.length == 1) return fields[0];
                if (fields.length == 0)
                    throw new HandlerError("No fields found in class " + cls.getName() + " cannot serialize it @AsString");
                return null;
            },
            (cls, fieldName) -> {
                Field[] fields = cls.getDeclaredFields();
                Field result = null;
                for (Field field : fields) {
                    if (Wrappers.isAssignable(field.getType(),
                            getAnnotationType())) {
                        if (result != null) return null;
                        result = field;
                    }
                }
                return result;
            }
    );

    static void save(SerializerTask task, Field field, Object instance, GenericType<?> expectedType) {
        Class<?> cls = field.getDeclaringClass();
        try {
            Object rawValue = field.get(instance);

            if (Wrappers.isAssignable(field.getType(), expectedType.getTypeClass())) {
                task.getResult().accept(rawValue);
                return;
            }

            task.serialize(rawValue, expectedType, task.getResult());
        } catch (IllegalAccessException e) {
            throw new HandlerError("An error occurs while accessing the field " + field.getName() + " in class " + cls.getName(), e);
        }
    }

    Field getField(Class<?> cls, String name) {
        for (BiFunction<Class<?>, String, Field> function : FUNCTIONS) {
            Field field = function.apply(cls, name);
            if (field != null) {
                SLogger.LOGGER.debug("Field chosen to serialize " + cls.getName() + " @AsString: " + field.getName());
                field.setAccessible(true);
                return field;
            }
        }
        throw new HandlerError("No way found to serialize the " + cls.getName() + " annotated with @AsString. Cannot identify the field.");
    }

    static <T extends Annotation> T getAnnotation(SerializerTask task, Class<T> cls) {
        T ann = task.getObject().getClass().getAnnotation(cls);

        if (ann == null) {
            throw new HandlerError("An error occurs while processing " + task + ". The object is not annotated with @" + cls.getName() + ". Make sure you call #canHandle(...) before #serialize(...)");
        }

        return ann;
    }

    public abstract Class<T> getAnnotationType();
}
