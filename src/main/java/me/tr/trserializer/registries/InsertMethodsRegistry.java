package me.tr.trserializer.registries;

import me.tr.trserializer.processes.process.insert.BaseInsert;
import me.tr.trserializer.processes.process.insert.InsertMethod;
import me.tr.trserializer.processes.process.insert.UnwrapInsert;
import me.tr.trserializer.processes.process.insert.WrapInsert;

import java.util.LinkedHashMap;
import java.util.Map;


public class InsertMethodsRegistry extends Registry<Class<? extends InsertMethod>, InsertMethod> {
    private static InsertMethodsRegistry instance = new InsertMethodsRegistry();
    private final Map<Class<? extends InsertMethod>, InsertMethod> inserts = new LinkedHashMap<>();


    public static InsertMethodsRegistry getInstance() {
        if (instance == null) {
            instance = new InsertMethodsRegistry();
        }
        return instance;
    }

    private InsertMethodsRegistry() {
        register(BaseInsert.class, new BaseInsert());
        register(UnwrapInsert.class, new UnwrapInsert());
        register(WrapInsert.class, new WrapInsert());
    }

    public static InsertMethod getMethod(Class<? extends InsertMethod> clazz) {
        return getInstance().get(clazz);
    }

    public Map<Class<? extends InsertMethod>, InsertMethod> getRegistry() {
        return inserts;
    }
}
