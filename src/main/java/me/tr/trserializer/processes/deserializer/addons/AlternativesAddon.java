package me.tr.trserializer.processes.deserializer.addons;

import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.Priority;
import me.tr.trserializer.types.GenericType;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class AlternativesAddon extends DeserializerAddon {

    public AlternativesAddon() {
        super("Alternatives", Priority.HIGH);
    }

    @Override
    public Optional<Object> process(Deserializer deserializer, Object obj,
                                    GenericType<?> type) throws Exception {

        Map<Class<?>, Function<Object, Optional<Class<?>>>> alternatives =
                deserializer.getOptions().getAlternatives();

        if (alternatives.containsKey(type.getTypeClass())) {
            Optional<Class<?>> alternative = alternatives.get(type.getTypeClass()).apply(obj);

            return alternative.map(aClass -> deserializer.deserialize(obj, aClass));
        }

        return Optional.empty();
    }
}
