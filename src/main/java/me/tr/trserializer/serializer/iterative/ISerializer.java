package me.tr.trserializer.serializer.iterative;

import me.tr.trserializer.exceptions.SerializationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.Serializer;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ISerializer implements Serializer<ISerializerTask> {

    @Override
    public <T> T serialize(Object object, GenericType<T> type) throws SerializationError, TypeMissMatched {
        AtomicReference<Object> result = new AtomicReference<>();
        Stack<SerializerTask> tasks = createStack(object, type, result::set);

        while (!tasks.isEmpty()) {
            SerializerTask task = tasks.pop();
            serialize(task);
        }

        return verifyResult(result, type);
    }

    public void serialize(ISerializerTask task) throws SerializationError, TypeMissMatched {
        if (task == null) return;

        if (task.getResult().hasResult()) {
            Object result = task.getResult().getResult();
            task.getResult().accept(result);
        } else if (task.getSavabilityChecker().isSavable()) {
            Object result = task.getObject();
            task.getResult().accept(result);
        } else if (task.getGenericType().isKeyObjectMap()) {
            Map<String, Object> result = serializeAsMap(task);
            task.getResult().accept(result);
        } else
            throw new SerializationError("Serializer cannot serialize " + task + ". Unknown object type, create a custom handler.");
    }

    @Override
    public Map<String, Object> serializeAsMap(ISerializerTask task) throws SerializationError, TypeMissMatched {
        Map<String, Object> result = new HashMap<>();
        Object instance = task.getObject();
        List<Field> fields = task.getFieldsRetriever().getFields();

        for (Field field : fields) {
            String name = field.getName();
            try {
                Object value = field.get(instance);

                new ISerializerFieldTask(task.getSerializer(), value, (o) -> result.put(name, o), task.getTasks(), field)
                        .schedule();
            } catch (IllegalAccessException | ExceptionInInitializerError e) {
                throw new SerializationError("An error occurs while accessing to field " + name + " in class " + task.getObjectClassName(), e);
            }
        }

        return result;
    }

    private Stack<SerializerTask> createStack(Object object, GenericType<?> type, Consumer<Object> root) {
        Stack<SerializerTask> stack = new Stack<>();
        ISerializerTask task = new ISerializerTask(UUID.randomUUID(), this, object, type, root, stack);
        stack.push(task);
        return stack;
    }

    @SuppressWarnings("unchecked")
    private <T> T verifyResult(AtomicReference<Object> result, GenericType<T> type) throws SerializationError, TypeMissMatched {
        Object value = result.get();

        if (value == null) {
            // TODO: Option to allow returning null.
            throw new TypeMissMatched("The found value is null. Expected: " + type);
        }

        Class<?> cls = value.getClass();
        Class<T> expected = type.getTypeClass();

        if (!expected.isAssignableFrom(cls)) {
            throw new TypeMissMatched("The found value is not convertible to the expected type! Expected: " + type + "; " +
                    "Received: " + Utility.getClassName(value));
        }

        // Safe to cast
        return (T) value;
    }
}
