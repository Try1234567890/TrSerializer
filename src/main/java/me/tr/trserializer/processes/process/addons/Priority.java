package me.tr.trserializer.processes.process.addons;

public enum Priority {

    VERY_LOW(5),
    LOW(4),
    NORMAL(3),
    HIGH(2),
    VERY_HIGH(1),
    MAX_PRIORITY(0);

    private final int code;

    Priority(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
