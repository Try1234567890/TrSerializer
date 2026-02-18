package me.tr.trserializer.processes.serializer.helper.insert;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.utility.Utility;

import java.util.Map;

public class UnwrapInsert implements InsertMethod {

    @Override
    public void insert(String key, Object value, Map<String, Object> map) {
        if (!Utility.isAMapWithStringKeys(value)) {
            throw new TypeMissMatched("The provided value is not a map.");
        }


        //noinspection unchecked
        map.putAll((Map<String, Object>) value);
    }
}
