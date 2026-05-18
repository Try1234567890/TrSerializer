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

    public Object instance() {
        if (instance == null) return instance = getInstancer().instance(getGenericType().getTypeClass());
        return instance;
    }

    public boolean isObjectAMapStringObject() {
        if (!(getObject() instanceof Map<?, ?> map)) return false;

        Object firstKeyNonNull = Utility.getFirstKeyNonNull(map);
        Class<?> keyCls = firstKeyNonNull == null ? Object.class : firstKeyNonNull.getClass();

        return Wrappers.isAssignable(keyCls, String.class);
    }

    public DeserializerTaskAssignabilityChecker getAssignabilityChecker() {
        return assignabilityChecker;
    }

    public TranslatorTaskFieldsRetriever getFieldsRetriever() {
        return fieldsRetriever;
    }

    @Override
    public String toString() {
        return "DeserializerTask[ID: " + id + ", Object: " + object + ", Type: " + type + ", Result: " + result + ", Instance: " + instance + "]";
    }
}
