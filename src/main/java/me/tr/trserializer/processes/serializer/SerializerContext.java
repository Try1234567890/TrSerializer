package me.tr.trserializer.processes.serializer;

import me.tr.trserializer.processes.process.ProcessContext;
import me.tr.trserializer.processes.serializer.addons.SDateAnnAddon;
import me.tr.trserializer.processes.serializer.addons.SHandlerAddon;
import me.tr.trserializer.processes.serializer.addons.SUnwrapAddon;
import me.tr.trserializer.processes.serializer.addons.SWrapAddon;
import me.tr.trserializer.processes.serializer.helper.SValueRetriever;

import java.util.List;

public class SerializerContext extends ProcessContext {
    private final SValueRetriever valueRetriever;

    public SerializerContext(Serializer serializer) {
        super(serializer, new SerializerCache(serializer), new SerializerOptions(serializer));
        this.valueRetriever = new SValueRetriever(serializer);
        getAddons().addAll(List.of(new SUnwrapAddon(), new SWrapAddon(), new SDateAnnAddon(), new SHandlerAddon()));
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

    public SValueRetriever getValueRetriever() {
        return valueRetriever;
    }
}
