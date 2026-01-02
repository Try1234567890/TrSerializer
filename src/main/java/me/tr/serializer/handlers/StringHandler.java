package me.tr.serializer.handlers;

import me.tr.serializer.types.GenericType;

public class StringHandler implements TypeHandler {

    @Override
    public String deserialize(Object obj, GenericType<?> type) {
        if (obj instanceof char[] chars) {
            return new String(chars);
        }

        if (obj instanceof byte[] bytes) {
            return new String(bytes);
        }

        return String.valueOf(obj);
    }

    @Override
    public String serialize(Object obj, GenericType<?> type) {
        return String.valueOf(obj);
    }

}
