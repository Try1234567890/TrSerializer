package me.tr.trserializer.registries;

import me.tr.trserializer.converters.BooleanConverter;
import me.tr.trserializer.converters.CharacterConverter;
import me.tr.trserializer.converters.Converter;
import me.tr.trserializer.converters.numbers.*;
import me.tr.trserializer.logger.Logger;

import java.util.LinkedHashMap;
import java.util.Map;


public class ConvertersRegistry extends Registry<Map.Entry<Class<?>, Class<?>>, Converter<?, ?>> {
    private static ConvertersRegistry instance = new ConvertersRegistry();
    private final Map<Map.Entry<Class<?>, Class<?>>, Converter<?, ?>> converters = new LinkedHashMap<>();


    public static ConvertersRegistry getInstance() {
        if (instance == null) {
            instance = new ConvertersRegistry();
        }
        return instance;
    }

    private ConvertersRegistry() {
        ByteConverter BYTE_CONVERTER = new ByteConverter();
        IntegerConverter INTEGER_CONVERTER = new IntegerConverter();
        LongConverter LONG_CONVERTER = new LongConverter();
        DoubleConverter DOUBLE_CONVERTER = new DoubleConverter();
        FloatConverter FLOAT_CONVERTER = new FloatConverter();
        ShortConverter SHORT_CONVERTER = new ShortConverter();
        BooleanConverter BOOLEAN_CONVERTER = new BooleanConverter();
        CharacterConverter CHARACTER_CONVERTER = new CharacterConverter();

        register(Map.entry(Number.class, Byte.class), BYTE_CONVERTER);
        register(Map.entry(Byte.class, Number.class), BYTE_CONVERTER);

        register(Map.entry(Number.class, Integer.class), INTEGER_CONVERTER);
        register(Map.entry(Integer.class, Number.class), INTEGER_CONVERTER);

        register(Map.entry(Number.class, Long.class), LONG_CONVERTER);
        register(Map.entry(Long.class, Number.class), LONG_CONVERTER);

        register(Map.entry(Number.class, Double.class), DOUBLE_CONVERTER);
        register(Map.entry(Double.class, Number.class), DOUBLE_CONVERTER);

        register(Map.entry(Number.class, Float.class), FLOAT_CONVERTER);
        register(Map.entry(Float.class, Number.class), FLOAT_CONVERTER);

        register(Map.entry(Number.class, Short.class), SHORT_CONVERTER);
        register(Map.entry(Short.class, Number.class), SHORT_CONVERTER);

        register(Map.entry(Boolean.class, Number.class), BOOLEAN_CONVERTER);
        register(Map.entry(Number.class, Boolean.class), BOOLEAN_CONVERTER);

        register(Map.entry(Character.class, Number.class), CHARACTER_CONVERTER);
        register(Map.entry(Number.class, Character.class), CHARACTER_CONVERTER);
    }

    @Override
    public Converter<?, ?> get(Map.Entry<Class<?>, Class<?>> name) {
        Converter<?, ?> bySimpleGet = super.get(name);
        if (bySimpleGet != null) {
            Logger.dbg("The converter for " + name + " is found with simple search.");
            return bySimpleGet;

        } else {
            Class<?> searchedKey = name.getKey();
            Class<?> searchedValue = name.getValue();

            for (Map.Entry<Map.Entry<Class<?>, Class<?>>, Converter<?, ?>> entry : getRegistry().entrySet()) {

                Map.Entry<Class<?>, Class<?>> subEntry = entry.getKey();

                if (subEntry.getKey().isAssignableFrom(searchedKey) &&
                        subEntry.getValue().isAssignableFrom(searchedValue)) {
                    Logger.dbg("The converter for " + name + " is found with complex search.");
                    return entry.getValue();
                }

            }
        }

        Logger.dbg("The converter for " + name + " not found.");
        return null;
    }

    @SuppressWarnings("unchecked")
    public <C, P> Converter<C, P> get(Class<C> from, Class<P> to) {
        return (Converter<C, P>) get(Map.entry(from, to));
    }

    public static <C, P> Converter<C, P> getConverter(Class<C> from, Class<P> to) {
        return getInstance().get(from, to);
    }

    @Override
    public boolean equals(Map.Entry<Class<?>, Class<?>> k1, Map.Entry<Class<?>, Class<?>> k2) {
        return k1.getKey().equals(k2.getKey()) &&
                k1.getValue().equals(k2.getValue());
    }

    @Override
    public Map<Map.Entry<Class<?>, Class<?>>, Converter<?, ?>> getRegistry() {
        return converters;
    }
}
