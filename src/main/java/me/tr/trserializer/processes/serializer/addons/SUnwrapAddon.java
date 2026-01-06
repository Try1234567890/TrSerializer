package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.util.Optional;

public class SUnwrapAddon extends SAddon {
    public SUnwrapAddon() {
        super("Unwrap");
    }

    @Override
    public Optional<Object> process(Serializer serializer, Object obj, GenericType<?> type) throws Exception {
        return Optional.empty();
    }
}
