package me.tr.trserializer.translator.resultVerifier;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.translator.Translator;
import me.tr.trserializer.types.GenericType;


/**
 * The result verifier is a system that ensure the correct type
 * of final results in any translator.
 * This system does not correct any errors but throws an exception
 * if any error occurs.
 * <p>
 * This implementation contains a {@link Translator} that can be used
 * to include its options in the verification process.
 */
public class TranslatorResultVerifier implements ResultVerifier {
    public final Translator translator;

    public TranslatorResultVerifier(Translator translator) {
        this.translator = translator;
    }

    /**
     * @return the translator that owns this result verifier
     */
    public Translator getTranslator() {
        return translator;
    }

    /**
     * Verify if the {@code result} is assignable from the {@code type}.
     * This method includes verification of this instance translator options too.
     *
     * @param result The result to check
     * @param type   The type expected
     * @param <T>    The type of the final result
     * @return If verification ends successfully the cast result, otherwise an {@link TypeMissMatched} is thrown.
     * @see StaticResultVerifier
     */
    @Override
    public <T> T verify(Object result, GenericType<T> type) throws TypeMissMatched {
        // TODO: Add translator option filter

        return StaticResultVerifier.check(result, type);
    }
}
