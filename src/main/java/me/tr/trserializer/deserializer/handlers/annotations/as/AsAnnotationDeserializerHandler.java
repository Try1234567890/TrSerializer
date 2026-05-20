package me.tr.trserializer.deserializer.handlers.annotations.as;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.SLogger;
import me.tr.trserializer.utility.Wrappers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public abstract class AsAnnotationDeserializerHandler<T> implements DeserializerHandler {
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


    Field getField(Class<?> cls, String name) {
        for (BiFunction<Class<?>, String, Field> function : FUNCTIONS) {
            Field field = function.apply(cls, name);
            if (field != null) {
                SLogger.LOGGER.debug("Field chosen to deserialize " + cls.getName() + " @As" + getAnnotationType().getName() + ": " + field.getName());
                field.setAccessible(true);
                return field;
            }
        }
        throw new HandlerError("No way found to deserialize the " + cls.getName() + " annotated with @As" + getAnnotationType().getName() + ". Cannot identify the field.");
    }


    static <T extends Annotation> T getAnnotation(DeserializerTask task, Class<T> cls) {
        T ann = task.getGenericType().getTypeClass().getAnnotation(cls);

        if (ann == null) {
            throw new HandlerError("An error occurs while processing " + task + ". The object is not annotated with @" + cls.getName() + ". Make sure you call #canHandle(...) before #deserialize(...)");
        }

        return ann;
    }


    static void setField(DeserializerTask task, Field field, Object value) {
        Class<?> fieldCls = field.getType();
        Class<?> valueCls = value.getClass();

        if (Wrappers.isAssignable(fieldCls, valueCls)) {
            try {
                Object instance = task.instance();
                field.set(instance, value);
                task.getResult().accept(instance);
                return;
            } catch (IllegalAccessException e) {
                throw new HandlerError("An error occurs while assigning value to the field " + field.getName() + " in class " + field.getDeclaringClass().getName(), e);
            }
        }

        task.deserialize(value, new GenericType<>(field), o -> setField(task, field, o));
    }

    public abstract Class<T> getAnnotationType();
}
