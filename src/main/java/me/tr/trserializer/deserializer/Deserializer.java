package me.tr.trserializer.deserializer;

import me.tr.trserializer.exceptions.DeserializationError;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.translator.Translator;
import me.tr.trserializer.types.GenericType;

public interface Deserializer extends Translator {

    <T> T deserialize(Object object, GenericType<T> type) throws DeserializationError, TypeMissMatched;

    default <T> T deserialize(Object object, Class<T> type) throws DeserializationError, TypeMissMatched {
        return deserialize(object, GenericType.of(type));
    }

    @Override
    default <T> T translate(Object object, GenericType<T> type) throws TranslationError, TypeMissMatched {
        return deserialize(object, type);
    }

    @Override
    DeserializerContext getContext();
}
