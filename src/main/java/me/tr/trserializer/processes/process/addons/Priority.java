package me.tr.trserializer.processes.process.addons;

public enum Priority {

    VERY_LOW(0),
    LOW(1),
    NORMAL(2),
    HIGH(3),
    VERY_HIGH(4),
    INFINITE(-1);

    private final int code;

    Priority(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
