package me.tr.trserializer.processes.process.helper;

import me.tr.trserializer.annotations.naming.Naming;
import me.tr.trserializer.annotations.naming.NamingStrategy;
import me.tr.trserializer.processes.process.Process;

import java.lang.reflect.Field;
import java.util.Optional;

public class NamingStrategyApplier {
    private final Process process;


    public NamingStrategyApplier(Process process) {
        this.process = process;
    }

    /**
     * Applies the naming strategy to a field's name based on {@link Naming} annotations.
     *
     * @param field the field whose name should be transformed.
     * @return the transformed field name.
     */
    public String applyNamingStrategy(Field field) {
        String fieldName = field.getName();
        Optional<Naming> annotation = getNamingAnn(field);

        if (annotation.isPresent())
            return applyNamingStrategy(fieldName, annotation.get());

        return fieldName;
    }

    /**
     * Checks for the presence of a {@link Naming} annotation on the field or its declaring class.
     */
    public Optional<Naming> getNamingAnn(Field field) {
        if (field.isAnnotationPresent(Naming.class)) {
            return Optional.of(field.getAnnotation(Naming.class));
        }

        Class<?> declaring = field.getDeclaringClass();
        if (declaring.isAnnotationPresent(Naming.class)) {
            return Optional.of(declaring.getAnnotation(Naming.class));
        }

        return Optional.empty();
    }

    /**
     * Formats a name using the specified {@link Naming} annotation settings.
     *
     * @param name the original name.
     * @param ann  the naming annotation containing strategies.
     * @return the formatted name.
     */
    public String applyNamingStrategy(String name, Naming ann) {
        NamingStrategy strategy = ann.strategy();

        NamingStrategy from = ann.from();

        if (strategy == NamingStrategy.NOTHING) {
            getProcess().getLogger().warn("The strategy of @Naming on " + name + " is null. Ignoring it.");
            return name;
        }


        if (from == NamingStrategy.NOTHING)
            return strategy.format(name);

        return strategy.format(name, from.getFormat());
    }

    public Process getProcess() {
        return process;
    }
}
