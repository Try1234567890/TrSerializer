package me.tr.trserializer.deserializer;

import me.tr.trserializer.instancer.TranslatorInstancer;
import me.tr.trserializer.translator.Translator;
import me.tr.trserializer.translator.TranslatorContext;
import me.tr.trserializer.translator.resultVerifier.TranslatorResultVerifier;

public class DeserializerContext extends TranslatorContext {


    public DeserializerContext(Translator translator, TranslatorResultVerifier resultVerifier, TranslatorInstancer instancer) {
        super(translator, resultVerifier, instancer);
    }

    public DeserializerContext(Translator translator) {
        super(translator);
    }

    @Override
    public Deserializer getTranslator() {
        return (Deserializer) super.getTranslator();
    }

    public Deserializer getDeserializer() {
        return (Deserializer) super.getTranslator();
    }

}
