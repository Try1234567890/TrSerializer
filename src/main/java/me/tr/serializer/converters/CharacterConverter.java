package me.tr.serializer.converters;

public class CharacterConverter implements Converter<Character, Integer> {
    @Override
    public Character complex(Integer primitive) {
        return (char) primitive.intValue();
    }

    @Override
    public Integer primitive(Character complex) {
        return (int) complex;
    }
}
