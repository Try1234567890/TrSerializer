package me.tr.serializer.converters.numbers;

public class DoubleConverter extends NumberConverter<Double> {

    @Override
    public Double primitive(Number complex) {
        return complex.doubleValue();
    }
}
