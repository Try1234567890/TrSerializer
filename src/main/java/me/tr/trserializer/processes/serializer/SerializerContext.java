package me.tr.trserializer.processes.serializer;

import me.tr.trserializer.processes.process.ProcessContext;
import me.tr.trserializer.processes.serializer.addons.SerializerHandlerAddon;

public class SerializerContext extends ProcessContext {

    public SerializerContext(Serializer serializer) {
        super(serializer, new SerializerCache(serializer), new SerializerOptions(serializer));
        getAddons().add(new SerializerHandlerAddon());
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
