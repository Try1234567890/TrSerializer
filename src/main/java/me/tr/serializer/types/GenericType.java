package me.tr.serializer.types;

import me.tr.serializer.utility.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

public class GenericType<T> implements ParameterizedType {
    private final Type rawType;
    private final Class<?> clazz;
    private Type[] typeArguments;

    public GenericType(Type rawType, Type... typeArguments) {
        this.rawType = rawType;
        this.clazz = (Class<?>) rawType;
        this.typeArguments = getTypeArguments(typeArguments);
    }

    public GenericType(Field field) {
        this.rawType = field.getType();
        this.clazz = (Class<?>) rawType;
        this.typeArguments = getTypeArguments(field);
    }

    private Type[] getTypeArguments(Field field) {
        Class<?> component = field.getType().getComponentType();
        if (component != null)
            return new Type[]{component};

        Type generic = field.getGenericType();
        if (generic instanceof ParameterizedType par)
            return par.getActualTypeArguments();

        return getTypeArguments(generic);
    }

    private Type[] getTypeArguments(Type... typeArguments) {
        if (typeArguments == null || typeArguments.length == 0) {
            if (clazz.isArray()) {
                return new Type[]{clazz.getComponentType()};
            }
            if (Collection.class.isAssignableFrom(clazz)) {
                return new Type[]{asClass(rawType.getTypeName())};
            }
        }
        return typeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments;
    }

    public Class<?> getFirstType() {
        Type type = typeArguments[0];
        return type instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) type).getRawType() : (Class<?>) type;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    public Class<?>  getClazz() {
        return clazz;
    }

    @Override
    public Type getOwnerType() {
        return clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ParameterizedType that)) return false;
        return rawType.equals(that.getRawType()) &&
                Arrays.equals(typeArguments, that.getActualTypeArguments());
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
