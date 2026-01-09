package me.tr.trserializer.processes.serializer.helper.insert;

import me.tr.trserializer.registries.InsertMethodsRegistry;

import java.util.Map;

public interface InsertMethod {

    InsertMethod BASE = get(BaseInsert.class);
    InsertMethod WRAP = get(WrapInsert.class);
    InsertMethod UNWRAP = get(UnwrapInsert.class);

    static InsertMethod get(Class<? extends InsertMethod> clazz) {
        return InsertMethodsRegistry.getMethod(clazz);
    }

    void insert(String key, Object value, Map<String, Object> map);

}
