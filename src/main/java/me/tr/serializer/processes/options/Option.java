package me.tr.serializer.processes.options;

public class Option<T> {
    private final Options name;
    private final Class<T> type;
    private T value;


    @SuppressWarnings("unchecked")
    public Option(Options name, T value) {
        this.name = name;
        this.type = (Class<T>) value.getClass();
        this.value = value;
    }


    public Options getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Class<T> getType() {
        return type;
    }
}
