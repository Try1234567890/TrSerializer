package me.tr.trserializer.handlers.java;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.types.GenericType;

public class StringHandler implements TypeHandler {

    @Override
    public String deserialize(Object obj, GenericType<?> type) {
        if (obj instanceof char[] chars) {
            return new String(chars);
        }

        if (obj instanceof byte[] bytes) {
            return new String(bytes);
        }

        if (obj instanceof StringBuffer buff) {
            return new String(buff);
        }

        if (obj instanceof StringBuilder build) {
            return new String(build);
        }

        return String.valueOf(obj);
    }

    @Override
    public String serialize(Object obj, GenericType<?> type) {
        return String.valueOf(obj);
    }

}
