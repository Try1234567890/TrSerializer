package me.tr.serializer.handlers.java;

import me.tr.serializer.converters.Converter;
import me.tr.serializer.exceptions.TypeMissMatched;
import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.logger.TrLogger;
import me.tr.serializer.registries.ConvertersRegistry;
import me.tr.serializer.types.GenericType;
import me.tr.serializer.utility.Utility;

public class PrimitiveHandler implements TypeHandler {

    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        Class<?> clazz = type.getTypeClass();

        if (obj instanceof Number num &&
                Utility.isWrapper(clazz)) {

            Converter<Number, ?> converter = ConvertersRegistry.getConverter(Number.class, clazz);
            if (converter != null)
                return converter.primitive(num);
        }

        return obj;
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        Class<?> clazz = type.getTypeClass();

        if (clazz.isPrimitive()) {
            return obj;
        }


        if (obj instanceof Number num &&
                Utility.isWrapper(clazz)) {

            Converter<Number, ?> converter = ConvertersRegistry.getConverter(Number.class, clazz);
            if (converter != null)
                return converter.primitive(num);

        }

        TrLogger.getInstance().exception(
                new TypeMissMatched("The provided object (" + clazz + ") is not a primitive type"));
        return null;
    }
}
