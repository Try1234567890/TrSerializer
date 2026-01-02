package me.tr.serializer.converters.numbers;

public class ByteConverter extends NumberConverter<Byte> {

    @Override
    public Byte primitive(Number complex) {
        return complex.byteValue();
    }
}
