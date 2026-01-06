package me.tr.trserializer.handlers.java;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.types.GenericType;

public class BooleanHandler implements TypeHandler {

    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        if (obj instanceof Number num) {
            return num.byteValue() == 1;
        }

        if (obj instanceof Boolean bool) {
            return bool;
        }

        return null;
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Number num) {
            return num.byteValue() == 1;
        }

        if (obj instanceof Boolean bool) {
            return bool;
        }

        return null;
    }
}
