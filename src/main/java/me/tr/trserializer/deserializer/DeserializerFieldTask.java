package me.tr.trserializer.deserializer;

import me.tr.trserializer.deserializer.helper.assignable.DeserializerFieldTaskAssignabilityChecker;
import me.tr.trserializer.deserializer.helper.typeResolver.DeserializerTaskTypeResolver;
import me.tr.trserializer.translator.FieldTask;

public interface DeserializerFieldTask extends FieldTask {

    DeserializerTaskTypeResolver getTypeResolver();

    DeserializerFieldTaskAssignabilityChecker getFieldAssignabilityChecker();

    default Deserializer getDeserializer() {
        return (Deserializer) getTranslator();
    }
}
