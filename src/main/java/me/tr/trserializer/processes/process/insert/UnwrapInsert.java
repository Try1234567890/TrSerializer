package me.tr.trserializer.processes.process.insert;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.logger.Logger;
import me.tr.trserializer.utility.Utility;

import java.util.Map;

public class UnwrapInsert implements InsertMethod {

    @Override
    public void insert(String key, Object value, Map<String, Object> map) {
        if (!Utility.isAMapWithStringKeys(value)) {
            Logger.exception(
                    new TypeMissMatched("The provided value is not a map."));
            return;
        }


        //noinspection unchecked
        map.putAll((Map<String, Object>) value);
    }
}
