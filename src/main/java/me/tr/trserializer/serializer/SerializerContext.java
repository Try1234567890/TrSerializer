package me.tr.trserializer.serializer;

import me.tr.trserializer.instancer.TranslatorInstancer;
import me.tr.trserializer.translator.Translator;
import me.tr.trserializer.translator.TranslatorContext;
import me.tr.trserializer.translator.resultVerifier.TranslatorResultVerifier;

/**
 * This class represent the context situation of a generic serializer.
 * The context of a serializer contains all system that can
 * be useful while serializing an object,
 * like: {@code result verifier} (to verify the final result)
 * or {@code instancer} (to instance classes)
 */
public class SerializerContext extends TranslatorContext {

    public SerializerContext(Translator translator, TranslatorResultVerifier resultVerifier, TranslatorInstancer instancer) {
        super(translator, resultVerifier, instancer);
    }

    public SerializerContext(Translator translator) {
        super(translator);
    }

    @Override
    public Serializer getTranslator() {
        return (Serializer) super.getTranslator();
    }

    public Serializer getSerializer() {
        return (Serializer) super.getTranslator();
    }


}
