package me.tr.serializer.converters.numbers;

public class IntegerConverter extends NumberConverter<Integer> {

    @Override
    public Integer primitive(Number complex) {
        return complex.intValue();
    }
}
