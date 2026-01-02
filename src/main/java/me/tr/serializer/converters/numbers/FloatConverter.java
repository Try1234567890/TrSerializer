package me.tr.serializer.converters.numbers;

public class FloatConverter extends NumberConverter<Float> {

    @Override
    public Float primitive(Number complex) {
        return complex.floatValue();
    }
}
