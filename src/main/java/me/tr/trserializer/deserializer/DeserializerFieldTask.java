package me.tr.trserializer.deserializer;

import me.tr.trserializer.serializer.Serializer;
import me.tr.trserializer.translator.FieldTask;

public interface DeserializerFieldTask extends FieldTask {

    @Override
    Deserializer getTranslator();

    default Deserializer getSerializer() {
        return getTranslator();
    }
}
