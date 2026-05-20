package me.tr.trserializer.serializer;

import me.tr.trserializer.translator.FieldTask;

public interface SerializerFieldTask extends FieldTask {

    @Override
    Serializer getTranslator();

    default Serializer getSerializer() {
        return getTranslator();
    }
}
