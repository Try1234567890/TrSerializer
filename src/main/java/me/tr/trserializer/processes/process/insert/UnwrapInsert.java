package me.tr.trserializer.processes.process.insert;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.utility.Utility;

import java.util.Map;

public class UnwrapInsert implements InsertMethod {


    @Override
    public void insert(String key, Object value, Map<String, Object> map) {
        if (!(value instanceof Map<?, ?> unsafeMap)) {
            TrLogger.exception(
                    new TypeMissMatched("The provided object is not a map but " + (value == null ? "null" : value.getClass())));
            return;
        }

        if (!String.class.isAssignableFrom(Utility.getKeyType(unsafeMap))) {
            TrLogger.exception(new TypeMissMatched("The provided map keys type is not String.class"));
            return;
        }

        //noinspection unchecked
        map.putAll((Map<String, Object>) unsafeMap);
    }
}
