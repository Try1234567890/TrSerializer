package me.tr.trserializer.processes.serializer.helper.insert;

import java.util.Map;

public class BaseInsert implements InsertMethod {


    @Override
    public void insert(String key, Object value, Map<String, Object> map) {
        map.put(key, value);
    }
}
