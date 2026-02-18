package me.tr.trserializer.types;

import me.tr.trserializer.annotations.SerializeAs;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class SerializerGenericType<T> extends GenericType<T> {

    public SerializerGenericType(Field field) {
        super(field);
    }

    @Override
    protected Type getRawType(Field field) {
        return (field.getType().isPrimitive() || field.getGenericType() instanceof ParameterizedType) ?
                field.getType() :
                getSerializeType(field);
    }

    @Override
    protected Type[] getTypeArguments(Field field) {
        return new Type[]{getSerializeType(field)};
    }

    private Class<?> getSerializeType(Field field) {
        if (field == null) {
            return Object.class;
        }

        if (field.isAnnotationPresent(SerializeAs.class)) {
            return field.getAnnotation(SerializeAs.class).as();
        }

        return Object.class;
    }
}
