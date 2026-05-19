package me.tr.trserializer.deserializer;

import me.tr.trserializer.deserializer.helper.assignable.DeserializerTaskAssignabilityChecker;
import me.tr.trserializer.translator.Result;
import me.tr.trserializer.translator.TranslatorTask;
import me.tr.trserializer.translator.fieldsRetriever.TranslatorTaskFieldsRetriever;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;
import me.tr.trserializer.utility.Wrappers;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class represent a task for a generic deserializer.
 * This is a minor level of abstraction of the {@link TranslatorTask}
 * but is still an abstract class.
 * Various deserializer effective implementation should extend this
 * class for code integrity.
 * <p>
 * A serializer task contains various fields:
 * <ul>
 *     <li>UUID -> The task ID defined in TranslatorTask.</li>
 *     <li>Deserializer -> The deserializer reference that creates it.</li>
 *     <li>Object -> The object to serialize.</li>
 *     <li>GenericType -> The expected type of the final result.</li>
 *     <li>Result -> The result of this task; remains empty until task finished for implementation that does not guarantee immediate result.</li>
 *     <li>DeserializerTaskAssignabilityChecker -> Assignability checker system. (Verify the final result)</li>
 *     <li>TranslatorTaskFieldsRetriever -> Fields retriever system. (Retrieve and set accessible the fields)</li>
 * </ul>
 */

public abstract class DeserializerTask implements TranslatorTask {
    private final UUID id;
    private final Deserializer deserializer;
    private final Object object;
    private final GenericType<?> type;
    private final Result result;
    private Object instance;
    private final DeserializerTaskAssignabilityChecker assignabilityChecker;
    private final TranslatorTaskFieldsRetriever fieldsRetriever;

    public DeserializerTask(UUID id, Deserializer deserializer, Object object, GenericType<?> type, Consumer<Object> result) {
        this.id = id;
        this.deserializer = deserializer;
        this.object = object;
        this.type = type;
        this.result = new Result(this, result);
        this.assignabilityChecker = new DeserializerTaskAssignabilityChecker(this);
        this.fieldsRetriever = new TranslatorTaskFieldsRetriever(this);
    }

    public DeserializerTask(Deserializer deserializer, Object object, GenericType<?> type, Consumer<Object> result) {
        this(UUID.randomUUID(), deserializer, object, type, result);
    }

    public abstract void deserialize(Object object, GenericType<?> type, Consumer<Object> result);

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public Deserializer getTranslator() {
        return deserializer;
    }

    public Deserializer getDeserializer() {
        return deserializer;
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

    public String getObjectClassName() {
        return Utility.getClassName(getObject());
    }

    public Class<?> getObjectClass() {
        return getObject() == null ? Object.class : getObject().getClass();
    }

    /**
     * Instance the final result expect type class, obtained via {@link GenericType#getTypeClass()}
     *
     * @return the final result expect type class instance.
     */
    public Object instance() {
        if (instance == null) return instance = getInstancer().instance(getGenericType().getTypeClass());
        return instance;
    }

    /**
     * Checks if the provided object is a {@link Map} with {@link String} keys.
     * (This means that is a map that represents the serialized final object)
     *
     * @return {@code true} if is a {@link Map} with {@link String} key.
     */
    public boolean isAMapWithStringKeys() {
        if (!(getObject() instanceof Map<?, ?> map)) return false;

        Object firstKeyNonNull = Utility.getFirstKeyNonNull(map);
        Class<?> keyCls = firstKeyNonNull == null ? Object.class : firstKeyNonNull.getClass();

        return Wrappers.isAssignable(keyCls, String.class);
    }

    /**
     * @return the assignability checker system.
     */
    public DeserializerTaskAssignabilityChecker getAssignabilityChecker() {
        return assignabilityChecker;
    }

    /**
     * @return the fields retriever system.
     */
    public TranslatorTaskFieldsRetriever getFieldsRetriever() {
        return fieldsRetriever;
    }

    @Override
    public String toString() {
        return "DeserializerTask[ID: " + id + ", Object: " + object + ", Type: " + type + ", Result: " + result + ", Instance: " + instance + "]";
    }
}
