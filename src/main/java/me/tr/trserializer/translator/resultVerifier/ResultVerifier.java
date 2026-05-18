package me.tr.trserializer.translator.resultVerifier;

import me.tr.trserializer.types.GenericType;

/**
 * The result verifier is a system that ensure the correct type
 * of final results in any translator.
 * This system does not correct any errors but throws an exception
 * if any error occurs.
 */
public interface ResultVerifier {

    <T> T verify(Object result, GenericType<T> type);

}
