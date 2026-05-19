package me.tr.trserializer.serializer;

import me.tr.trserializer.serializer.helpers.savable.SerializerTaskSavabilityChecker;
import me.tr.trserializer.serializer.helpers.typeResolver.StaticSerializerTypeResolver;
import me.tr.trserializer.translator.Result;
import me.tr.trserializer.translator.TranslatorTask;
import me.tr.trserializer.translator.fieldsRetriever.TranslatorTaskFieldsRetriever;
import me.tr.trserializer.types.GenericType;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class represent a task for a generic serializer.
 * This is a minor level of abstraction of the {@link TranslatorTask}
 * but is still an abstract class.
 * Various serializer effective implementation should extend this
 * class for code integrity.
 * <p>
 * A serializer task contains various fields:
 * <ul>
 *     <li>UUID -> The task ID defined in TranslatorTask.</li>
 *     <li>Serializer -> The serializer reference that creates it.</li>
 *     <li>Object -> The object to serialize.</li>
 *     <li>GenericType -> The expected type of the final result.</li>
 *     <li>Result -> The result of this task; remains empty until task finished for implementation that does not guarantee immediate result.</li>
 *     <li>SerializerTaskSavabilityChecker -> Savability checker system. (Verify the final result)</li>
 *     <li>TranslatorTaskFieldsRetriever -> Fields retriever system. (Retrieve and set accessible the fields)</li>
 * </ul>
 */
public abstract class SerializerTask implements TranslatorTask {
    private final UUID id;
    private final Serializer serializer;
    private final Object object;
    private final GenericType<?> type;
    private final Result result;
    private final SerializerTaskSavabilityChecker savabilityChecker;
    private final TranslatorTaskFieldsRetriever fieldsRetriever;

    public SerializerTask(UUID id, Serializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                          SerializerTaskSavabilityChecker savabilityChecker) {
        this.id = id;
        this.serializer = serializer;
        this.object = object;
        this.type = type;
        this.result = new Result(this, result);
        this.savabilityChecker = savabilityChecker;
        this.fieldsRetriever = new TranslatorTaskFieldsRetriever(this);
    }

    public SerializerTask(Serializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                          SerializerTaskSavabilityChecker savabilityChecker) {
        this(UUID.randomUUID(), serializer, object, type, result, savabilityChecker);
    }

    public SerializerTask(UUID id, Serializer serializer, Object object, GenericType<?> type, Consumer<Object> result) {
        this.id = id;
        this.serializer = serializer;
        this.object = object;
        this.type = type;
        this.result = new Result(this, result);
        this.savabilityChecker = new SerializerTaskSavabilityChecker(this);
        this.fieldsRetriever = new TranslatorTaskFieldsRetriever(this);
    }

    public SerializerTask(Serializer serializer, Object object, GenericType<?> type, Consumer<Object> result) {
        this(UUID.randomUUID(), serializer, object, type, result);
    }

    public SerializerTask(Serializer serializer, Object object, Consumer<Object> result) {
        this(UUID.randomUUID(), serializer, object, StaticSerializerTypeResolver.resolve(object), result);
    }

    /**
     * This method handles the sub-serializing process.
     * Is useful for handler or addons to serialize an object
     * with the same strategy of the implementation without knowing it.
     * In fact all effective implementation define it and handle this process
     * for the handler or addon.
     *
     * @param object The object to process.
     * @param type   The expected type of final result.
     * @param result The consumer to call when the result is complete.
     */
    public abstract void serialize(Object object, GenericType<?> type, Consumer<Object> result);

    /**
     * This method handles the sub-serializing process.
     * Is useful for handler or addons to serialize an object
     * with the same strategy of the implementation without knowing it.
     * In fact all effective implementation define it and handle this process
     * for the handler or addon.
     * <p>
     * The expected type of final result is resolved with {@link StaticSerializerTypeResolver}.
     *
     * @param object The object to process.
     * @param result The consumer to call when the result is complete.
     */
    public void serialize(Object object, Consumer<Object> result) {
        serialize(object, StaticSerializerTypeResolver.resolve(object), result);
    }

    /**
     * This method handles the sub-serializing process.
     * Is useful for handler or addons to serialize an object
     * with the same strategy of the implementation without knowing it.
     * In fact all effective implementation define it and handle this process
     * for the handler or addon.
     * <p>
     * The expected type of final result is resolved with {@link StaticSerializerTypeResolver}
     * and the result is the same as this task.
     *
     * @param object The object to process.
     */
    public void serialize(Object object) {
        serialize(object, StaticSerializerTypeResolver.resolve(object), getResult());
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public Serializer getTranslator() {
        return serializer;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public GenericType<?> getGenericType() {
        return type;
    }

    @Override
    public Result getResult() {
        return result;
    }

    /**
     * @return The savability checker system
     */
    public SerializerTaskSavabilityChecker getSavabilityChecker() {
        return savabilityChecker;
    }

    /**
     * @return The fields retriever system
     */
    public TranslatorTaskFieldsRetriever getFieldsRetriever() {
        return fieldsRetriever;
    }

    @Override
    public String toString() {
        return "SerializerTask[ID=" + id + ", Object=" + object + ", Type=" + type + ", Result=" + result + ']';
    }
}
