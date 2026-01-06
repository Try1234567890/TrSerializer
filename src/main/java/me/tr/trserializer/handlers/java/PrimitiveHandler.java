package me.tr.trserializer.handlers.java;

import me.tr.trserializer.converters.Converter;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.registries.ConvertersRegistry;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

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

        TrLogger.exception(new TypeMissMatched("The provided object (" + clazz + ") is not a primitive type"));
        return null;
    }
}
