package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.handlers.dates.DateHandlerContainer;
import me.tr.trserializer.processes.process.addons.date.PDateAnnMethods;
import me.tr.trserializer.processes.process.addons.handler.PHandlerMethods;
import me.tr.trserializer.processes.process.addons.date.PDateAnnAddon;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public class SDateAnnAddon extends SAddon {
    private final PDateAnnAddon ann;

    public SDateAnnAddon() {
        super("date-annotation");
        this.ann = new PDateAnnAddon(new PDateAnnMethods() {
            @Override
            public Object execute(DateHandlerContainer handler, Object obj, GenericType<?> type) {
                return handler.serialize(obj, type);
            }

            @Override
            public Class<?> getClassForHandler(Object obj, GenericType<?> type) {
                return obj == null ? Object.class : obj.getClass();
            }
        });
    }


    @Override
    public Optional<Object> process(Serializer process, Object obj, GenericType<?> type, Field field) throws ProcessError {
        return ann.process(process, obj, type, field);
    }
}





















