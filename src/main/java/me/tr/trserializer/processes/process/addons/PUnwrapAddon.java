package me.tr.trserializer.processes.process.addons;

import me.tr.trserializer.annotations.unwrap.Unwrap;
import me.tr.trserializer.annotations.unwrap.Unwrapped;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.insert.UnwrapInsert;
import me.tr.trserializer.registries.InsertMethodsRegistry;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PUnwrapAddon extends PAddon {

    public PUnwrapAddon() {
        super("Unwrap", Priority.HIGH, InsertMethodsRegistry.getMethod(UnwrapInsert.class));
    }


    protected Set<Field> getFields(Process process, Unwrapped unwrapped, Object instance) {
        Set<Field> fields = new HashSet<>();
        Set<String> fieldNamesFromAnn = Arrays.stream(unwrapped.fields()).collect(Collectors.toSet());

        for (Field field : process.getFields(instance.getClass())) {
            String fieldName = field.getName();

            if (field.isAnnotationPresent(Unwrap.class) ||
                    fieldNamesFromAnn.contains(fieldName)) {
                fields.add(field);
            }
        }

        return fields;
    }
}
