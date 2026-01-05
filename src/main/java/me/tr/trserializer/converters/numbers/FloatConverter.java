package me.tr.trserializer.converters.numbers;

public class FloatConverter extends NumberConverter<Float> {

    @Override
    public Float primitive(Number complex) {
        return complex.floatValue();
    }
}
