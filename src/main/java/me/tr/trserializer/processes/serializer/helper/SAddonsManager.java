package me.tr.trserializer.processes.serializer.helper;

import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.processes.process.helper.AddonsManager;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.processes.serializer.addons.SAddon;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

public class SAddonsManager extends AddonsManager {

    public SAddonsManager(Serializer serializer) {
        super(serializer);
    }

    public Optional<Map.Entry<SAddon, ?>> getValidAddon(Object obj, GenericType<?> type, Field field) {
        Optional<Map.Entry<PAddon, ?>> addon = super.getFirstValidAddon(obj, type, field);

        if (addon.isPresent()) {
            Map.Entry<PAddon, ?> entry = addon.get();

            if (entry.getKey() instanceof SAddon serAddon) {
                return Optional.of(Map.entry(serAddon, entry.getValue()));
            }

        }

        return Optional.empty();
    }

    public Optional<Map.Entry<SAddon, ?>> getValidAddon(Object obj, GenericType<?> type) {
        return getValidAddon(obj, type, null);
    }


    @Override
    public Serializer getProcess() {
        return (Serializer) super.getProcess();
    }

    public Serializer getSerializer() {
        return getProcess();
    }
}
