package me.tr.trserializer.translator.resultVerifier;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.SLogger;
import me.tr.trserializer.utility.Utility;
import me.tr.trserializer.utility.Wrappers;

/**
 * The result verifier is a system that ensure the correct type
 * of final results in any translator.
 * This system does not correct any errors but throws an exception
 * if any error occurs.
 * <p>
 * This implementation is singleton and perform a generic verification
 * without external information.
 */
public class StaticResultVerifier implements ResultVerifier {
    public static final StaticResultVerifier INSTANCE = new StaticResultVerifier();

    private StaticResultVerifier() {

    }

    public static StaticResultVerifier getInstance() {
        return INSTANCE;
    }

    public static <T> T check(Object result, GenericType<T> type) {
        return INSTANCE.verify(result, type);
    }


    /**
     * Verify if the {@code result} is assignable from the {@code type}
     *
     * @param result The result to check
     * @param type   The type expected
     * @param <T>    The type of the final result
     * @return If verification ends successfully the cast result, otherwise an {@link TypeMissMatched} is thrown.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T verify(Object result, GenericType<T> type) throws TypeMissMatched {
        if (result == null) {
            // TODO: Option to allow returning null.
            throw new TypeMissMatched("The found value is null. Expected: " + type);
        }

        Class<?> cls = result.getClass();
        Class<T> expected = type.getTypeClass();

        SLogger.LOGGER.debug("Verifying " + cls.getName() + " and " + expected.getName());

        if (!Wrappers.isAssignable(expected, cls)) {
            throw new TypeMissMatched("The found value is not convertible to the expected type! Expected: " + type + "; " +
                    "Received: " + Utility.getClassName(result));
        }

        // Safe to cast
        return (T) result;
    }
}
