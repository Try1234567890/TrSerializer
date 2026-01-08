package me.tr.trserializer.processes.deserializer;

import me.tr.trserializer.processes.deserializer.addons.DDateAnnAddon;
import me.tr.trserializer.processes.deserializer.addons.DHandlerAddon;
import me.tr.trserializer.processes.deserializer.addons.DUnwrapAddon;
import me.tr.trserializer.processes.deserializer.addons.DWrapAddon;
import me.tr.trserializer.processes.deserializer.helper.DValueRetriever;
import me.tr.trserializer.processes.deserializer.helper.DValueSetter;
import me.tr.trserializer.processes.process.ProcessContext;

import java.util.List;

public class DeserializerContext extends ProcessContext {
    private final DValueRetriever valueRetriever;
    private final DValueSetter valueSetter;

    public DeserializerContext(Deserializer deserializer) {
        super(deserializer, new DeserializerCache(deserializer), new DeserializerOptions(deserializer));
        this.valueRetriever = new DValueRetriever(deserializer);
        this.valueSetter = new DValueSetter(deserializer);
        getAddons().addAll(List.of(new DUnwrapAddon(), new DWrapAddon(), new DDateAnnAddon(), new DHandlerAddon()));
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

    public DValueRetriever getValueRetriever() {
        return valueRetriever;
    }

    public DValueSetter getValueSetter() {
        return valueSetter;
    }
}
