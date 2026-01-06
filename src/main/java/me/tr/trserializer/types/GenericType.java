package me.tr.trserializer.types;

import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericType<T> implements ParameterizedType {
    private final Type rawType;
    private final Class<?> typeClass;
    private final Type[] typeArguments;

    public GenericType(Type rawType, Type... typeArguments) {
        this.rawType = rawType;
        this.typeClass = rawType instanceof Class<?> ?
                (Class<?>) rawType : asClass(rawType.getTypeName());
        this.typeArguments = typeArguments;
    }

    public GenericType(Field field) {
        this.rawType = getRawType(field);
        this.typeClass = (Class<?>) rawType;
        this.typeArguments = getTypeArguments(field);
    }

    public static <T> GenericType<T> of(Class<T> clazz) {
        return new GenericType<>(clazz);
    }

    protected Type getRawType(Field field) {
        return field.getType();
    }

    protected Type[] getTypeArguments(Field field) {
        Class<?> component = field.getType().getComponentType();
        if (component != null) // Is an array
            return new Type[]{component};

        Type generic = field.getGenericType();

        // Is a collection, a Map, an AtomicReference
        // or something that has generics.
        if (generic instanceof ParameterizedType par) {
            return par.getActualTypeArguments();
        }

        // Something else...
        return new Type[]{generic};
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments;
    }

    public Class<?> getFirstArgumentType() {
        Type type = typeArguments.length > 0 ? typeArguments[0] : Object.class;
        return type instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) type).getRawType() : (Class<?>) type;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    @Override
    public Type getOwnerType() {
        return getTypeClass();
    }

    @Override
    public String toString() {
        return getTypeClass().getName();
    }

    public static Class<?> asClass(String type) {
        try {
            String name = Utility.removeGeneric(type);
            return Class.forName(name, true, ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException ignored) {
            return Object.class;
        }
    }
}
