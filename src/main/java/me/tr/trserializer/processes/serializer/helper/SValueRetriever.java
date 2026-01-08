package me.tr.trserializer.processes.serializer.helper;

import me.tr.trformatter.strings.format.formats.CamelCase;
import me.tr.trformatter.strings.format.formats.PascalCase;
import me.tr.trserializer.annotations.Getter;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.processes.serializer.SerializerOptions;
import me.tr.trserializer.utility.Three;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SValueRetriever {
    private final Serializer serializer;

    public SValueRetriever(Serializer serializer) {
        this.serializer = serializer;
    }

    public SerializerOptions getOptions() {
        return getSerializer().getOptions();
    }

    public String getMapKey(Field field) {
        Class<?> declaringClass = field.getDeclaringClass();
        String fieldName = field.getName();

        for (Three<Class<?>, String, String> three : getOptions().getAliases()) {
            if (three.key().equals(declaringClass) &&
                    three.value().equals(fieldName)) {
                fieldName = three.subValue();
            }
        }

        return getSerializer().getNamingStrategyApplier().applyNamingStrategy(field);
    }

    public Object getValueOf(Field field, Object instance) throws IllegalAccessException, InvocationTargetException {
        if (field.isAnnotationPresent(Getter.class)) {
            Class<?> instanceClass = instance.getClass();
            Getter ann = field.getAnnotation(Getter.class);
            String fieldName = field.getName();
            String[] methodNames = {
                    ann.name(),
                    "get" + new PascalCase(fieldName).toCaseFrom(CamelCase.class).getResult(),
                    fieldName
            };

            for (String methodName : methodNames) {
                if (methodName == null || methodName.isEmpty()) continue;
                try {
                    Method method = instanceClass.getDeclaredMethod(methodName);
                    method.setAccessible(true);
                    return method.invoke(instance);
                } catch (NoSuchMethodException ignore) {
                }
            }
            getSerializer().getLogger().throwable(new NoSuchMethodException("No methods found in class " + Utility.getClassName(instanceClass) + " with names " + Arrays.toString(methodNames) + ". Retrieving value from the field directly..."));
        }

        return field.get(instance);
    }

    public Serializer getSerializer() {
        return serializer;
    }
}
