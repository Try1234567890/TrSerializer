package me.tr.trserializer.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a utility class that contains all
 * defaults value for classes where {@code null} causes error.
 * Classes like: {@link Integer} (or {@code int}), {@link Byte}  (or {@code byte}), {@link Collection} or {@link Map}
 */
public class DefaultValues {
    private DefaultValues() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility classes.");
    }

    private static final Map<Class<?>, Object> DEFAULTS = new HashMap<>();

    // Initialize the default value on first call.
    static {
        DEFAULTS.put(int.class, -1);
        DEFAULTS.put(Integer.class, -1);
        DEFAULTS.put(byte.class, (byte) -1);
        DEFAULTS.put(Byte.class, (byte) -1);
        DEFAULTS.put(short.class, (short) -1);
        DEFAULTS.put(Short.class, (short) -1);
        DEFAULTS.put(long.class, -1L);
        DEFAULTS.put(Long.class, -1L);
        DEFAULTS.put(float.class, -1.0f);
        DEFAULTS.put(Float.class, -1.0f);
        DEFAULTS.put(double.class, -1.0d);
        DEFAULTS.put(Double.class, -1.0d);
        DEFAULTS.put(char.class, '\000');
        DEFAULTS.put(Character.class, '\000');
        DEFAULTS.put(String.class, "");
        DEFAULTS.put(Collection.class, new ArrayList<>());
        DEFAULTS.put(Map.class, new HashMap<>());
    }

    /**
     * Retrieve the default value for {@code class}.
     *
     * @param cls The class to retrieve def-value for.
     * @return The default value if found, otherwise {@code null}.
     */
    public static Object getDefaultValue(Class<?> cls) {
        return DEFAULTS.get(cls);
    }
}
