package me.tr.serializer.registries;

import me.tr.serializer.converters.BooleanConverter;
import me.tr.serializer.converters.CharacterConverter;
import me.tr.serializer.converters.Converter;
import me.tr.serializer.converters.numbers.*;

import java.util.LinkedHashMap;
import java.util.Map;


public class ConvertersRegistry {
    private static ConvertersRegistry instance = new ConvertersRegistry();
    private final Map<Map.Entry<Class<?>, Class<?>>, Converter<?, ?>> converters = new LinkedHashMap<>();

    public static ConvertersRegistry getInstance() {
        if (instance == null) {
            instance = new ConvertersRegistry();
        }
        return instance;
    }

    private ConvertersRegistry() {
        converters.put(Map.entry(Number.class, Byte.class), new ByteConverter());
        converters.put(Map.entry(Number.class, byte.class), new ByteConverter());
        converters.put(Map.entry(Number.class, Integer.class), new IntegerConverter());
        converters.put(Map.entry(Number.class, int.class), new IntegerConverter());
        converters.put(Map.entry(Number.class, Long.class), new LongConverter());
        converters.put(Map.entry(Number.class, long.class), new LongConverter());
        converters.put(Map.entry(Number.class, Double.class), new DoubleConverter());
        converters.put(Map.entry(Number.class, double.class), new DoubleConverter());
        converters.put(Map.entry(Number.class, Float.class), new FloatConverter());
        converters.put(Map.entry(Number.class, float.class), new FloatConverter());
        converters.put(Map.entry(Number.class, Short.class), new ShortConverter());
        converters.put(Map.entry(Number.class, short.class), new ShortConverter());
        converters.put(Map.entry(Boolean.class, Byte.class), new BooleanConverter());
        converters.put(Map.entry(boolean.class, Byte.class), new BooleanConverter());
        converters.put(Map.entry(Character.class, Integer.class), new CharacterConverter());
        converters.put(Map.entry(char.class, Integer.class), new CharacterConverter());
    }

    @SuppressWarnings("unchecked")
    public <C, P> Converter<C, P> get(Class<C> from, Class<P> to) {
        Converter<?, ?> exactMatch = converters.get(Map.entry(from, to));
        if (exactMatch != null) return (Converter<C, P>) exactMatch;

        for (Map.Entry<Map.Entry<Class<?>, Class<?>>, Converter<?, ?>> entry : converters.entrySet()) {
            Class<?> registeredFrom = entry.getKey().getKey();
            Class<?> registeredTo = entry.getKey().getValue();

            if (registeredFrom.isAssignableFrom(from) && registeredTo.equals(to)) {
                return (Converter<C, P>) entry.getValue();
            }
        }

        return null;
    }

    public static <C, P> Converter<C, P> getConverter(Class<C> from, Class<P> to) {
        return getInstance().get(from, to);
    }

    public static ByteConverter getByteConverter() {
        return (ByteConverter) getConverter(Number.class, Byte.class);
    }

    public static IntegerConverter getIntegerConverter() {
        return (IntegerConverter) getConverter(Number.class, Integer.class);
    }

    public static LongConverter getLongConverter() {
        return (LongConverter) getConverter(Number.class, Long.class);
    }

    public static DoubleConverter getDoubleConverter() {
        return (DoubleConverter) getConverter(Number.class, Double.class);
    }

    public static FloatConverter getFloatConverter() {
        return (FloatConverter) getConverter(Number.class, Float.class);
    }

    public static ShortConverter getShortConverter() {
        return (ShortConverter) getConverter(Number.class, Short.class);
    }

    public static Converter<Boolean, Byte> getBooleanConverter() {
        return getConverter(Boolean.class, Byte.class);
    }

    public static Converter<Character, Integer> getCharacterConverter() {
        return getConverter(Character.class, Integer.class);
    }
}
