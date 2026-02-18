package me.tr.trserializer.processes.deserializer.addons;

import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.handlers.dates.DateHandlerContainer;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.addons.date.PDateAnnAddon;
import me.tr.trserializer.processes.process.addons.date.PDateAnnMethods;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public class DDateAnnAddon extends DAddon {
    private final PDateAnnAddon ann;

    public DDateAnnAddon() {
        super("date-annotation");
        this.ann = new PDateAnnAddon(new PDateAnnMethods() {
            @Override
            public Object execute(DateHandlerContainer handler, Object obj, GenericType<?> type) {
                return handler.deserialize(obj, type);
            }

            @Override
            public Class<?> getClassForHandler(Object obj, GenericType<?> type) {
                return type.getTypeClass();
            }
        });
    }

    @Override
    public Optional<Object> process(Deserializer process, Object obj, GenericType<?> type, Field field) throws ProcessError {
        return ann.process(process, obj, type, field);
    }
}





















