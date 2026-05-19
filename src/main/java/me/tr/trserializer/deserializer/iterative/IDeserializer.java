package me.tr.trserializer.deserializer.iterative;

import me.tr.trserializer.deserializer.Deserializer;
import me.tr.trserializer.deserializer.DeserializerContext;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.DeserializationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.registries.DeserializerHandlers;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class IDeserializer implements Deserializer {
    private final DeserializerContext context;

    public IDeserializer(DeserializerContext context) {
        this.context = context;
    }

    public IDeserializer() {
        this.context = new DeserializerContext(this);
    }

    @Override
    public <T> T deserialize(Object object, GenericType<T> type) throws DeserializationError, TypeMissMatched {
        AtomicReference<Object> result = new AtomicReference<>();
        Stack<IDeserializerTask> tasks = createStack(object, type, result::set);

        while (!tasks.isEmpty()) {
            IDeserializerTask task = tasks.pop();
            deserialize(task);
        }

        return verifyResult(result, type);
    }

    public void deserialize(IDeserializerTask task) {
        if (task == null) return;
        Optional<DeserializerHandler> handler = DeserializerHandlers.getHandlerFor(task);
        if (handler.isPresent()) {
            DeserializerHandler handlerInstance = handler.get();
            handlerInstance.deserialize(task);
        } else if (task.getAssignabilityChecker().isAssignable()) {
            Object result = task.getObject();
            task.getResult().accept(result);
        } else if (task.isAMapWithStringKeys()) {
            Object result = deserializeFromMap(task);
            task.getResult().accept(result);
        } else
            throw new DeserializationError("Deserializer cannot deserialize " + task + ". Unknown object type, create a custom handler");
    }

    @SuppressWarnings("unchecked")
    public Object deserializeFromMap(IDeserializerTask task) throws DeserializationError {
        List<Field> fields = task.getFieldsRetriever().getFields(task.getGenericType().getTypeClass());
        Map<String, Object> object = (Map<String, Object>) task.getObject(); // safe to cast
        Object instance = task.instance();

        for (Field field : fields) {
            String name = field.getName();
            Object rawValue = object.get(name);

            new IDeserializerFieldTask(task.getDeserializer(), rawValue, (o) -> set(field, instance, o), task.getTasks(), field)
                    .schedule();
        }

        return instance;
    }

    private void set(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new DeserializationError("An error occurs while assigning the value to " + field.getName(), e);
        }
    }

    private <T> T verifyResult(AtomicReference<Object> result, GenericType<T> type) throws TypeMissMatched {
        return getContext().getResultVerifier().verify(result.get(), type);
    }

    private Stack<IDeserializerTask> createStack(Object object, GenericType<?> type, Consumer<Object> root) {
        Stack<IDeserializerTask> stack = new Stack<>();
        IDeserializerTask task = new IDeserializerTask(this, object, type, root, stack);
        stack.push(task);
        return stack;
    }

    @Override
    public DeserializerContext getContext() {
        return context;
    }
}
