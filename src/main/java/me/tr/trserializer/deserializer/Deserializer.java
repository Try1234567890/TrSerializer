package me.tr.trserializer.deserializer;

import me.tr.trserializer.exceptions.DeserializationError;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.translator.Translator;
import me.tr.trserializer.types.GenericType;

/**
 * A Deserializer is a translator that converts {@link java.util.Map}s,
 * with String keys and Object value into POJOs classes.
 * <p>
 * While processing, an instance of the excepted result type
 * is created and that for each field is assigned the value
 * found in the provided {@link java.util.Map} with the key
 * equals to the field name.
 * </p>
 * <p>
 * So this simple map processed by the Deserialize:
 * <pre>
 * {
 *     "folder": "/home/name/procotols/45",
 *     "number": "45",
 *     "date": "01-01-2026",
 *     "sender": "name.surname@gmail.com",
 *     "receiver": "surname.name@gmail.com",
 *     "state": "SENT",
 *     "object": [
 *          "This is the object of this protocol",
 *          "The short object of this protocol"
 *     ],
 *     "files": [
 *          "/home/name/procotols/45/files/1.png",
 *          "/home/name/procotols/45/files/2.pdf",
 *          "/home/name/procotols/45/files/3.jpg",
 *          "/home/name/procotols/45/files/4.svg"
 *     ]
 * }
 * </pre>
 * Will be:
 * <pre>
 * public class Protocol {
 *     private final File folder = /home/name/procotols/45;
 *     private final int number = 45;
 *     private Date date = 01-01-2026;
 *     private String sender = "name.surname@gmail.com";
 *     private String receiver = "surname.name@gmail.com";
 *     private State state = SENT; // This is an enumerator
 *     private String[] object = ["This is the object of this protocol", "The short object of this protocol"];
 *     private List<ProtocolFile> files = ["/home/name/procotols/45/files/1.png", "/home/name/procotols/45/files/2.pdf", "/home/name/procotols/45/files/3.jpg", "/home/name/procotols/45/files/4.svg"];
 * }
 * </pre>
 */
public interface Deserializer extends Translator {

    /**
     * Deserialize the {@code object} as the {@code type}
     *
     * @param object The object to process
     * @param type   The excepted final result type
     * @param <T>    The type excepted
     * @return An instance of the excepted type filled with provided information.
     * @throws DeserializationError If an error occurs while deserialization process.
     * @throws TypeMissMatched      If the final result isn't assignable from the excepted type.
     */
    <T> T deserialize(Object object, GenericType<T> type) throws DeserializationError, TypeMissMatched;

    default <T> T deserialize(Object object, Class<T> type) throws DeserializationError, TypeMissMatched {
        return deserialize(object, GenericType.of(type));
    }

    @Override
    default <T> T translate(Object object, GenericType<T> type) throws TranslationError, TypeMissMatched {
        return deserialize(object, type);
    }

    @Override
    DeserializerContext getContext();
}
