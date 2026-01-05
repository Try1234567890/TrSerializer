package me.tr.trserializer.converters.numbers;

public class DoubleConverter extends NumberConverter<Double> {

    @Override
    public Double primitive(Number complex) {
        return complex.doubleValue();
    }
}
