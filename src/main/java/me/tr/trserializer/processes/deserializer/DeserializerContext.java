package me.tr.trserializer.processes.deserializer;

import me.tr.trserializer.processes.deserializer.addons.DeserializerDateAnnotationAddon;
import me.tr.trserializer.processes.deserializer.addons.DeserializerHandlerAddon;
import me.tr.trserializer.processes.process.ProcessContext;

import java.util.List;

public class DeserializerContext extends ProcessContext {

    public DeserializerContext(Deserializer serializer) {
        super(serializer, new DeserializerCache(serializer), new DeserializerOptions(serializer));
        getAddons().addAll(List.of(
                        new DeserializerHandlerAddon(),
                        new DeserializerDateAnnotationAddon()));
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
