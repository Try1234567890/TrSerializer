package me.tr.trserializer.processes.deserializer;

import me.tr.trserializer.processes.deserializer.addons.DDateAnnAddon;
import me.tr.trserializer.processes.deserializer.addons.DHandlerAddon;
import me.tr.trserializer.processes.deserializer.addons.DUnwrapAddon;
import me.tr.trserializer.processes.process.ProcessContext;

import java.util.List;

public class DeserializerContext extends ProcessContext {

    public DeserializerContext(Deserializer serializer) {
        super(serializer, new DeserializerCache(serializer), new DeserializerOptions(serializer));
        getAddons().addAll(List.of(new DUnwrapAddon(), new DDateAnnAddon(), new DHandlerAddon()));
    }

    @Override
    public Deserializer getProcess() {
        return (Deserializer) super.getProcess();
    }

    @Override
    public DeserializerCache getCache() {
        return (DeserializerCache) super.getCache();
    }

    @Override
    public DeserializerOptions getOptions() {
        return (DeserializerOptions) super.getOptions();
    }
}
