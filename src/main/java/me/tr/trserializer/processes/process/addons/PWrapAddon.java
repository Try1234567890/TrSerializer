package me.tr.trserializer.processes.process.addons;

import me.tr.trserializer.processes.process.insert.WrapInsert;
import me.tr.trserializer.registries.InsertMethodsRegistry;

public abstract class PWrapAddon extends PAddon {
    public PWrapAddon() {
        super("wrapper", Priority.HIGH, InsertMethodsRegistry.getMethod(WrapInsert.class));
    }
}
