package me.tr.trserializer.serializer;

import me.tr.trserializer.exceptions.SerializationError;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.translator.Translator;
import me.tr.trserializer.types.GenericType;

import java.util.Map;

/**
 * A Serialize is a translator that converts POJOs classes
 * into a {@link java.util.Map}, with String keys and Object value.
 * <p>
 * While processing the {@link java.util.Map} is filled with
 * fields name as map key and the respective, already translated,
 * field value.
 * </p>
 * <p>
 * So this simple class processed by the Serialize:
 * <pre>
 * public class Protocol {
 *     private final File folder;
 *     private final int number;
 *     private Date date;
 *     private String sender;
 *     private String receiver;
 *     private State state; // This is an enumerator
 *     private String[] object;
 *     private List<ProtocolFile> files;
 * }
 * </pre>
 * <p>
 * Will be:
 * <pre>
 *     {
 *         "folder": "/home/name/procotols/45",
 *         "number": "45",
 *         "date": "01-01-2026",
 *         "sender": "name.surname@gmail.com",
 *         "receiver": "surname.name@gmail.com",
 *         "state": "SENT",
 *         "object": [
 *              "This is the object of this protocol",
 *              "The short object of this protocol"
 *         ],
 *         "files": [
 *              "/home/name/procotols/45/files/1.png",
 *              "/home/name/procotols/45/files/2.pdf",
 *              "/home/name/procotols/45/files/3.jpg",
 *              "/home/name/procotols/45/files/4.svg"
 *         ]
 *     }
 * </pre>
 */
public interface Serializer extends Translator {

    /**
     * Serialize the provided object as the provided {@link GenericType}
     *
     * @param object The object to serialize.
     * @param type   The expected result type.
     * @param <T>    The expected result type.
     * @return The result type.
     * @throws SerializationError if some error occurs while serializing the object.
     * @throws TypeMissMatched    if the final result is not assignable from the {@code type}.
     */
    <T> T serialize(Object object, GenericType<T> type) throws SerializationError, TypeMissMatched;

    @Override
    default <T> T translate(Object object, GenericType<T> type) throws TranslationError, TypeMissMatched {
        return serialize(object, type);
    }

    /**
     * Serialize the provided object as the provided {@link GenericType}
     *
     * @param object The object to serialize.
     * @param cls    The expected result type.
     * @param <T>    The expected result type.
     * @return The result type.
     * @throws SerializationError if some error occurs while serializing the object.
     * @throws TypeMissMatched    if the final result is not assignable from the {@code type}.
     */
    default <T> T serialize(Object object, Class<T> cls) throws SerializationError, TypeMissMatched {
        return serialize(object, new GenericType<>(cls));
    }

    /**
     * Serialize the provided object.
     *
     * @param object The object to serialize.
     * @return The result type.
     * @throws SerializationError if some error occurs while serializing the object.
     */
    default Map<String, Object> serialize(Object object) throws SerializationError {
        return serialize(object, new GenericType<>(Map.class, String.class, Object.class));
    }

    @Override
    SerializerContext getContext();
}
