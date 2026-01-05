package me.tr.trserializer.converters;

public class BooleanConverter implements Converter<Boolean, Byte> {
    @Override
    public Boolean complex(Byte primitive) {
        return primitive == 1;
    }

    @Override
    public Byte primitive(Boolean complex) {
        return (byte) (complex ? 1 : 0);
    }
}
