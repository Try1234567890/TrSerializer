package me.tr.trserializer.translator;

import me.tr.trserializer.types.GenericType;

import java.util.UUID;

public interface TranslatorTask {

    UUID getID();

    Translator getTranslator();

    Object getObject();

    GenericType<?> getGenericType();

    Result getResult();

    default boolean isFieldTask() {
        return this instanceof TranslatorFieldTask;
    }
}
