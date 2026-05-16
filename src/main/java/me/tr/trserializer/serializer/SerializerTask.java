package me.tr.trserializer.serializer;

import me.tr.trserializer.serializer.helpers.fieldsRetriever.SerializerTaskFieldsRetriever;
import me.tr.trserializer.serializer.helpers.savable.SerializerTaskSavabilityChecker;
import me.tr.trserializer.serializer.helpers.typeResolver.StaticObjectSerializerTypeResolver;
import me.tr.trserializer.translator.Result;
import me.tr.trserializer.translator.TranslatorTask;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class SerializerTask implements TranslatorTask {
    private final UUID id;
    private final Serializer<?> serializer;
    private final Object object;
    private final GenericType<?> type;
    private final Result result;
    private final SerializerTaskSavabilityChecker savabilityChecker;
    private final SerializerTaskFieldsRetriever fieldsRetriever;

    public SerializerTask(UUID id, Serializer<?> serializer, Object object, GenericType<?> type, Consumer<Object> result,
                          SerializerTaskSavabilityChecker savabilityChecker) {
        this.id = id;
        this.serializer = serializer;
        this.object = object;
        this.type = type;
        this.result = new Result(this, result);
        this.savabilityChecker = savabilityChecker;
        this.fieldsRetriever = new SerializerTaskFieldsRetriever(this);
    }

    public SerializerTask(Serializer<?> serializer, Object object, GenericType<?> type, Consumer<Object> result,
                          SerializerTaskSavabilityChecker savabilityChecker) {
        this(UUID.randomUUID(), serializer, object, type, result, savabilityChecker);
    }

    public SerializerTask(UUID id, Serializer<?> serializer, Object object, GenericType<?> type, Consumer<Object> result) {
        this.id = id;
        this.serializer = serializer;
        this.object = object;
        this.type = type;
        this.result = new Result(this, result);
        this.savabilityChecker = new SerializerTaskSavabilityChecker(this);
        this.fieldsRetriever = new SerializerTaskFieldsRetriever(this);
    }

    public SerializerTask(Serializer<?> serializer, Object object, GenericType<?> type, Consumer<Object> result) {
        this(UUID.randomUUID(), serializer, object, type, result);
    }

    public SerializerTask(Serializer<?> serializer, Object object, Consumer<Object> result) {
        this(UUID.randomUUID(), serializer, object, StaticObjectSerializerTypeResolver.resolve(object), result);
    }

    public abstract void serialize(Object object, GenericType<?> type, Consumer<Object> result);

    public void serialize(Object object, Consumer<Object> result) {
        serialize(object, StaticObjectSerializerTypeResolver.resolve(object), result);
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

    public String getObjectClassName() {
        return Utility.getClassName(getObject());
    }

    public Class<?> getObjectClass() {
        return getObject() == null ? Object.class : getObject().getClass();
    }

    @Override
    public GenericType<?> getGenericType() {
        return type;
    }

    public SerializerTaskSavabilityChecker getSavabilityChecker() {
        return savabilityChecker;
    }

    public SerializerTaskFieldsRetriever getFieldsRetriever() {
        return fieldsRetriever;
    }

    @Override
    public Result getResult() {
        return result;
    }
}
