package me.tr.trserializer.instancer;

import me.tr.trserializer.options.MapOption;
import me.tr.trserializer.utility.Wrappers;

import java.util.*;
import java.util.function.Supplier;

/**
 * This class represent the options of a generic {@link Instancer}.
 */
public class InstancerOptions {
    /**
     * This {@link MapOption} contains all external instance methods.
     * When the instancer cannot instance a class, insert a manual
     * {@link Supplier} that returns a new instance of the class.
     */
    private final MapOption<Class<?>, Supplier<Object>> instanceMethods;

    public InstancerOptions(MapOption<Class<?>, Supplier<Object>> instanceMethods) {
        this.instanceMethods = instanceMethods;
    }

    public InstancerOptions() {
        this.instanceMethods = new MapOption<>(
                Map.ofEntries(
                        Map.entry(Set.class, HashSet::new),
                        Map.entry(List.class, ArrayList::new),
                        Map.entry(Map.class, HashMap::new),
                        Map.entry(String.class, String::new),
                        Map.entry(int.class, () -> -1),
                        Map.entry(Integer.class, () -> -1),
                        Map.entry(short.class, () -> (short) -1),
                        Map.entry(Short.class, () -> (short) -1),
                        Map.entry(byte.class, () -> (byte) -1),
                        Map.entry(Byte.class, () -> (byte) -1),
                        Map.entry(long.class, () -> -1L),
                        Map.entry(Long.class, () -> -1L),
                        Map.entry(float.class, () -> -1.0F),
                        Map.entry(Float.class, () -> -1.0F),
                        Map.entry(double.class, () -> -1.0D),
                        Map.entry(Double.class, () -> -1.0D),
                        Map.entry(char.class, () -> '\000'),
                        Map.entry(Character.class, () -> '\000')
                )
        );
    }

    public MapOption<Class<?>, Supplier<Object>> getInstanceMethods() {
        return instanceMethods;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getInstanceMethod(final Class<T> cls) {
        Supplier<Object> supplier = getInstanceMethods().get(c -> Wrappers.isAssignable(cls, c));
        return supplier == null ? Optional.empty() : Optional.of((T) supplier.get());
    }
}
