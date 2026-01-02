package me.tr.serializer.converters.numbers;

public class ShortConverter extends NumberConverter<Short> {

    @Override
    public Short primitive(Number complex) {
        return complex.shortValue();
    }
}
