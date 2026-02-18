package me.tr.trserializer.processes.deserializer.addons;

import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.addons.Priority;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DAlternativesAddon extends DAddon {

    public DAlternativesAddon() {
        super("Alternatives", Priority.VERY_HIGH);
    }

    @Override
    public Optional<Object> process(Deserializer deserializer, Object obj,
                                    GenericType<?> type, Field field) throws ProcessError {

        Map<Class<?>, Function<Object, Optional<Class<?>>>> alternatives = deserializer.getOptions().getAlternatives();

        Class<?> clazz = type.getTypeClass();

        if (alternatives.containsKey(clazz)) {
            return Optional.empty();
        }

        Optional<Class<?>> alternative = alternatives.get(clazz).apply(obj);

        if (alternative.isEmpty()) {
            return Optional.empty();
        }

        Class<?> newAlternative = alternative.get();

        if (newAlternative.equals(clazz)) {
            return Optional.empty();
        }

        return alternative.map(aClass -> deserializer.deserialize(obj, aClass));

    }
}
