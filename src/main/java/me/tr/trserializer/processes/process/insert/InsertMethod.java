package me.tr.trserializer.processes.process.insert;

import java.util.Map;

public interface InsertMethod {

    void insert(String key, Object value, Map<String, Object> map);

}
