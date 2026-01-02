package me.tr.serializer.converters.numbers;

import me.tr.serializer.converters.Converter;

public abstract class NumberConverter<N extends Number> implements Converter<Number, N> {

    @Override
    public Number complex(N primitive) {
        return primitive;
    }
}
