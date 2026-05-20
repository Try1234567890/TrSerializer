package me.tr.trserializer.serializer.iterative;

import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.exceptions.SerializationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.registries.SerializerHandlers;
import me.tr.trserializer.serializer.Serializer;
import me.tr.trserializer.serializer.SerializerContext;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.translator.resultVerifier.ResultVerifier;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.SLogger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * This class represent the iterative implementation
 * of the serializer.
 * An iterative serialize works by managing the stack
 * of tasks manually.
 * With that the iterative serializer has many advantages:
 * Is faster, is more flexible and the only limitation on
 * how many items can serialize or the max deep level
 * is the RAM assigned to the program.
 * <p>
 * At the moment (17/03/2026) this is the only default implementation.
 */
public class ISerializer implements Serializer {
    private final SerializerContext context;

    public ISerializer(SerializerContext context) {
        this.context = context;
    }

    public ISerializer() {
        this.context = new SerializerContext(this);
    }

    /**
     * Serialize the provided object.
     *
     * @param object The object to serialize.
     * @param type   The expected result type.
     * @param <T>    The final object type
     * @return The object serialized.
     * @throws SerializationError If any error occurs while serializing the task as a map.
     * @throws HandlerError       If any error occurs while execution of a handler.
     * @throws TypeMissMatched    If the verification of the final result fails.
     */
    @Override
    public <T> T serialize(Object object, GenericType<T> type) throws SerializationError, TypeMissMatched {
        AtomicReference<Object> result = new AtomicReference<>();
        Stack<ISerializerTask> tasks = createStack(object, type, result::set);

        while (!tasks.isEmpty()) {
            ISerializerTask task = tasks.pop();
            SLogger.LOGGER.debug("Processing: " + task);
            serialize(task);
        }

        return verifyResult(result, type);
    }

    /**
     * Serialize the {@code task} by handling in order:
     * <ol>
     *     <li>If any handler can process the object</li>
     *     <li>If the object is savable</li>
     *     <li>If the object is serializable as map</li>
     *     <li>Otherwise thrown a Serialization error</li>
     * </ol>
     *
     * @param task The task to handler.
     * @throws SerializationError If any error occurs while serializing the task as a map.
     * @throws HandlerError       If any error occurs while execution of a handler.
     * @throws TypeMissMatched    If the verification process on the final result fails.
     */
    public void serialize(ISerializerTask task) throws SerializationError, HandlerError, TypeMissMatched {
        if (task == null) return;
        Optional<SerializerHandler> handler = SerializerHandlers.getHandlerFor(task);
        if (handler.isPresent()) {
            SerializerHandler handlerInstance = handler.get();
            SLogger.LOGGER.debug("  Using the found handler: " + handlerInstance + " to serialize it.");
            handlerInstance.serialize(task);
        } else if (task.getSavabilityChecker().isSavable()) {
            SLogger.LOGGER.debug("  The object is already savable. Saving it.");
            Object result = task.getObject();
            task.getResult().accept(result);
        } else if (task.getGenericType().isKeyObjectMap()) {
            SLogger.LOGGER.debug("  The requested type is a Map. Serializing the object as a Map.");
            Map<String, Object> result = serializeAsMap(task);
            task.getResult().accept(result);
        } else
            throw new SerializationError("Serializer cannot serialize " + task + ". Unknown object type, create a custom handler.");
    }

    /**
     * Serialize the {@code task} as a String-Object map.
     * <p>
     * The result map contains as keys the fields name and
     * as values the fields value.
     *
     * @param task The task to handle.
     * @return The mapped fields as described above.
     * @throws SerializationError If an error occurs while serializing.
     */
    public Map<String, Object> serializeAsMap(ISerializerTask task) throws SerializationError {
        Map<String, Object> result = new HashMap<>();
        Object instance = task.getObject();
        List<Field> fields = task.getFieldsRetriever().getObjectFields();
        SLogger.LOGGER.debug("      Creating a map with " + fields.size() + " fields.");

        for (Field field : fields) {
            String name = field.getName();
            try {
                Object value = field.get(instance);

                new ISerializerFieldTask(task.getSerializer(), value, (o) -> result.put(name, o), task.getTasks(), field)
                        .schedule();
                SLogger.LOGGER.debug("      Serializer task scheduled for field " + name);
            } catch (IllegalAccessException | ExceptionInInitializerError e) {
                throw new SerializationError("An error occurs while accessing to field " + name + " in class " + task.getObjectClassName(), e);
            }
        }

        return result;
    }

    /**
     * Create the initial stack of the main process
     *
     * @param object The root object.
     * @param type   The expected type of the final result.
     * @param result The of the root task.
     * @return a new {@link Stack} of {@link ISerializerTask} with the root task.
     */
    private Stack<ISerializerTask> createStack(Object object, GenericType<?> type, Consumer<Object> result) {
        Stack<ISerializerTask> stack = new Stack<>();
        ISerializerTask task = new ISerializerTask(this, object, type, result, stack);
        stack.push(task);
        return stack;
    }

    /**
     * Verify the result via the {@link ResultVerifier#verify(Object, GenericType)}.
     *
     * @param result The result of the main process.
     * @param type   The excepted type of final result.
     * @param <T>    The type of the final result.
     * @return The verified and cast the final result.
     * @throws TypeMissMatched If the {@code result} is not assignable from the {@code type}
     */
    private <T> T verifyResult(AtomicReference<Object> result, GenericType<T> type) throws TypeMissMatched {
        return getContext().getResultVerifier().verify(result.get(), type);
    }

    @Override
    public SerializerContext getContext() {
        return context;
    }
}
