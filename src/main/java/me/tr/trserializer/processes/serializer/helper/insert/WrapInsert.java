package me.tr.trserializer.processes.serializer.helper.insert;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.utility.Utility;

import java.util.HashMap;
import java.util.Map;

public class WrapInsert implements InsertMethod {


    @Override
    public void insert(String key, Object value, Map<String, Object> map) {
        if (!Utility.isAMapWithStringKeys(value)) {
            throw new TypeMissMatched("The provided value is not a map.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> valueMap = (Map<String, Object>) value;

        if (valueMap.size() != 1) {
            throw new IllegalArgumentException("The provided map doesn't seams a wrapped one. Contains more the 1 entry.");
        }

        Map.Entry<String, Object> entry = valueMap.entrySet().iterator().next();
        String newKey = entry.getKey();
        Map<String, Object> entryValue = new HashMap<>(Map.of(key, entry.getValue()));
        Map<String, Object> newValue = entryValue;

        if (map.containsKey(newKey)) {
            Object oldValue = map.get(newKey);
            if (Utility.isAMapWithStringKeys(oldValue)) {
                newValue = new HashMap<>();
                newValue.putAll(entryValue);
                //noinspection unchecked
                newValue.putAll((Map<String, Object>) oldValue);
            }
        }

        map.put(newKey, newValue);
    }
}
