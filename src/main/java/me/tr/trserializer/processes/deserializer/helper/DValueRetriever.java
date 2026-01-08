package me.tr.trserializer.processes.deserializer.helper;

import me.tr.trserializer.annotations.Aliases;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.utility.Three;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DValueRetriever {
    private final Deserializer deserializer;


    public DValueRetriever(Deserializer deserializer) {
        this.deserializer = deserializer;
    }

    public Object getMapValue(Field field, Map<String, Object> map) {
        String fieldName = getDeserializer().getNamingStrategyApplier().applyNamingStrategy(field);

        if (map.containsKey(fieldName)) {
            return map.get(fieldName);
        }

        Set<String> aliases = getAliases(field);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (compare(fieldName, key) || aliases.contains(key)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public Set<String> getAliases(Field field) {
        Set<String> result = new HashSet<>();
        Class<?> declaringClass = field.getDeclaringClass();
        String fieldName = getDeserializer().getNamingStrategyApplier().applyNamingStrategy(field);

        for (Three<Class<?>, String, String[]> aliases : getDeserializer().getOptions().getAliases()) {
            if (aliases.key().equals(declaringClass) &&
                    compare(fieldName, aliases.value())) {
                result.addAll(List.of(aliases.subValue()));
                break;
            }
        }

        if (field.isAnnotationPresent(Aliases.class)) {
            Aliases ann = field.getAnnotation(Aliases.class);
            result.addAll(List.of(ann.aliases()));
        }

        return result;
    }

    private boolean compare(String s, String s2) {
        return getDeserializer().getOptions().isIgnoreCase() ? s.equalsIgnoreCase(s2) : s.equals(s2);
    }


    public Deserializer getDeserializer() {
        return deserializer;
    }
}
