package me.tr.trserializer.converters;

public interface Converter<C, P> {

    C complex(P primitive);

    P primitive(C complex);

}

