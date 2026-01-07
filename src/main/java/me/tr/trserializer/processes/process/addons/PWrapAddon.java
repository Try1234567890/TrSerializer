package me.tr.trserializer.processes.process.addons;

import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.insert.WrapInsert;
import me.tr.trserializer.registries.InsertMethodsRegistry;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public abstract class PWrapAddon extends PAddon {
    public PWrapAddon() {
        super("Wrap", Priority.HIGH, InsertMethodsRegistry.getMethod(WrapInsert.class));
    }
}
