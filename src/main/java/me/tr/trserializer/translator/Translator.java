package me.tr.trserializer.translator;

import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.instancer.TranslatorInstancer;
import me.tr.trserializer.types.GenericType;

/**
 * A translator is a generic system that convert an object
 * into its raw object and vice versa.
 * <p>
 * For example the default implemented two translator are the:
 * - Iterative-Serializer: That converts a known classes (POJOs, Java Basics or Custom Implementation) into their respective raw objects
 * - Iterative-Deserializer: That converts a raw objects into their respective known classes (POJOs, Java Basics or Custom Implementation)
 */
public interface Translator {

    /**
     * Translate the provided object as the provided {@link GenericType}
     *
     * @param object The object to translate.
     * @param type   The expected result type.
     * @param <T>    The expected result type.
     * @return The result type.
     * @throws TranslationError if some error occurs while translating the object.
     * @throws TypeMissMatched  if the final result is not assignable from the {@code type}.
     */
    <T> T translate(Object object, GenericType<T> type) throws TranslationError, TypeMissMatched;

    /**
     * Translate the provided object as the provided {@link GenericType}
     *
     * @param object The object to translate.
     * @param cls    The expected result type.
     * @param <T>    The expected result type.
     * @return The result type.
     * @throws TranslationError if some error occurs while translating the object.
     * @throws TypeMissMatched  if the final result is not assignable from the {@code type}.
     */
    default <T> T translate(Object object, Class<T> cls) throws TranslationError, TypeMissMatched {
        return translate(object, new GenericType<>(cls));
    }

    /**
     * @return the context of this translator
     */
    TranslatorContext getContext();


    /**
     * Short-cut for {@link TranslatorContext#getInstancer()}
     */
    default TranslatorInstancer getInstancer() {
        return getContext().getInstancer();
    }

}
