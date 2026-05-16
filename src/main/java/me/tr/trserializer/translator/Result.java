package me.tr.trserializer.translator;

import java.util.function.Consumer;

public class Result implements Consumer<Object> {
    private final TranslatorTask task;
    private final Consumer<Object> consumer;
    private Object result;

    public Result(TranslatorTask task, Consumer<Object> consumer) {
        this.task = task;
        this.consumer = consumer;
    }

    @Override
    public final void accept(Object initialResult) {
        if (hasResult()) return;
        Object processedResult = initialResult;

        // TODO: Add the addons & filter execution
        //       when respective managers are finished

        this.result = processedResult;
        getConsumer().accept(processedResult);
    }

    public final Object acceptAndReturn(Object initialResult) {
        if (hasResult()) return result;
        accept(initialResult);
        return result;
    }

    public TranslatorTask getTask() {
        return task;
    }

    public Consumer<Object> getConsumer() {
        return consumer;
    }

    public Object getResult() {
        return result;
    }

    public boolean hasResult() {
        return result != null;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
