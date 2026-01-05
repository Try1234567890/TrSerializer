package me.tr.trserializer.converters.numbers;

import me.tr.trserializer.converters.Converter;

public abstract class NumberConverter<N extends Number> implements Converter<Number, N> {

    @Override
    public Number complex(N primitive) {
        return primitive;
    }
}
