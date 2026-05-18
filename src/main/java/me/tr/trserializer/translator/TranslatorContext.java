package me.tr.trserializer.translator;

import me.tr.trserializer.instancer.TranslatorInstancer;
import me.tr.trserializer.translator.resultVerifier.TranslatorResultVerifier;

/**
 * This class represent the context situation
 * of a generic translator.
 * The context of a translator contains all system that can
 * be useful while processing an object,
 * like: {@code result verifier} (to verify the final result)
 * or {@code instancer} (to instance classes)
 */
public class TranslatorContext {
    private final Translator translator;
    private final TranslatorResultVerifier resultVerifier;
    private final TranslatorInstancer instancer;

    public TranslatorContext(Translator translator,
                             TranslatorResultVerifier resultVerifier,
                             TranslatorInstancer instancer) {
        this.translator = translator;
        this.resultVerifier = resultVerifier;
        this.instancer = instancer;
    }

    public TranslatorContext(Translator translator) {
        this(translator, new TranslatorResultVerifier(translator), new TranslatorInstancer(translator));
    }

    /**
     * @return the translator that owns this context.
     */
    public Translator getTranslator() {
        return translator;
    }

    /**
     * @return the result verifier system.
     */
    public TranslatorResultVerifier getResultVerifier() {
        return resultVerifier;
    }

    /**
     * @return the instancer system.
     */
    public TranslatorInstancer getInstancer() {
        return instancer;
    }
}
