package me.tr.serializer.converters.numbers;

public class LongConverter extends NumberConverter<Long> {

    @Override
    public Long primitive(Number complex) {
        return complex.longValue();
    }
}
