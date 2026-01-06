package me.tr.trserializer.processes.serializer;

import me.tr.trserializer.processes.process.ProcessContext;
import me.tr.trserializer.processes.serializer.addons.SerializerDateAnnotationAddon;
import me.tr.trserializer.processes.serializer.addons.SerializerHandlerAddon;

import java.util.List;

public class SerializerContext extends ProcessContext {

    public SerializerContext(Serializer serializer) {
        super(serializer, new SerializerCache(serializer), new SerializerOptions(serializer));
        getAddons().addAll(List.of(
                        new SerializerHandlerAddon(),
                        new SerializerDateAnnotationAddon()));
    }

    @Override
    public Serializer getProcess() {
        return (Serializer) super.getProcess();
    }

    @Override
    public SerializerCache getCache() {
        return (SerializerCache) super.getCache();
    }

    @Override
    public SerializerOptions getOptions() {
        return (SerializerOptions) super.getOptions();
    }
}
