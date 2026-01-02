package me.tr.serializer.handlers;

import me.tr.serializer.converters.Converter;
import me.tr.serializer.registries.ConvertersRegistry;
import me.tr.serializer.types.GenericType;
import me.tr.serializer.utility.Utility;

public class PrimitiveHandler implements TypeHandler {

    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        if (Utility.isWrapper(obj) && obj instanceof Number num) {
            Converter<Number, ?> converter = ConvertersRegistry.getConverter(Number.class, type.getClazz());
            if (converter != null)
                return converter.primitive(num);
        }

        return obj;
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        return obj;
    }
}
