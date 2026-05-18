package me.tr.trserializer.instancer;

import java.util.Map;

/**
 * This interface is used by the default implementation of the {@link Instancer}
 * to simply create a new instance of the "parent class".
 * <p>
 * For example, if class A implements {@link Instantiable} when the
 * default implementations process it simply class {@link Instantiable#instantiate(Map)}.
 */
public interface Instantiable {

    /**
     * Instantiate the current class with the {@code params}.
     *
     * @param params The params to provide to the method/constructor.
     * @return A new instance of the current class.
     */
    Object instantiate(Map<String, Object> params);

}
