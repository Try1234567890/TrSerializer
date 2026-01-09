package me.tr.trserializer.processes.deserializer.helper;

import me.tr.trserializer.annotations.unwrap.Unwrapped;
import me.tr.trserializer.annotations.wrap.Wrapped;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.deserializer.addons.DAddon;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.processes.process.helper.AddonsManager;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

public class DAddonsManager extends AddonsManager {

    public DAddonsManager(Deserializer deserializer) {
        super(deserializer);
    }


    /**
     * Resolves the first applicable addon for the given object and context,
     * considering potential wrapping or unwrapping logic.
     * <p>
     * This method determines which source data to provide to the addons based on the
     * state of the input and the presence of specific annotations:
     * <ul>
     * <li>If the source {@code obj} is null but a {@code values} map is present, and the field
     * is annotated with {@link Wrapped} or {@link Unwrapped}, it attempts to process
     * the addon using the values map.</li>
     * <li>If the source {@code obj} is directly available, it uses that for validation.</li>
     * </ul>
     * </p>
     *
     * @param obj    the original object instance; may be {@code null} if values are provided.
     * @param values a map of pre-processed values, used if the object is missing and
     *               wrapping/unwrapping is required.
     * @param type   the generic type metadata of the element being processed.
     * @param field  the field currently under evaluation.
     * @return an {@link Optional} containing the result of the first valid addon,
     * or {@code empty} if no addon is found or valid.
     */
    public Optional<Object> getValidAddon(Object obj, Map<String, Object> values, GenericType<?> type, Field field) {
        Optional<Map.Entry<PAddon, ?>> addon = Optional.empty();

        if ((obj == null && values != null) &&
                (field.isAnnotationPresent(Wrapped.class) ||
                        field.isAnnotationPresent(Unwrapped.class))) {

            addon = super.getFirstValidAddon(values, type, field);
        } else if (obj != null) {
            addon = super.getFirstValidAddon(obj, type, field);
        }

        if (addon.isPresent()) {
            return Optional.ofNullable(addon.get().getValue());
        }

        return Optional.empty();
    }

    /**
     * Resolves the first applicable addon for the given object instance.
     * <p>
     * This is a simplified version of {@link #getValidAddon(Object, Map, GenericType, Field)}
     * that performs a direct lookup based solely on the provided object.
     * </p>
     *
     * @param obj   the object instance to be validated against available addons.
     * @param type  the generic type metadata of the element.
     * @param field the field currently under evaluation.
     * @return an {@link Optional} containing the result of the first valid addon,
     * or {@code empty} if no addon is found or valid.
     */
    public Optional<Object> getValidAddon(Object obj, GenericType<?> type) {
        Optional<Map.Entry<PAddon, ?>> addon = super.getFirstValidAddon(obj, type, null);

        if (addon.isPresent()) {
            return Optional.ofNullable(addon.get().getValue());
        }

        return Optional.empty();
    }


    @Override
    public Deserializer getProcess() {
        return (Deserializer) super.getProcess();
    }

    public Deserializer getDeserializer() {
        return getProcess();
    }
}
