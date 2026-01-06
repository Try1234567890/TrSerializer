package me.tr.trserializer.handlers.dates;

import me.tr.trserializer.handlers.TypeHandler;

public abstract class DateHandlerContainer implements TypeHandler {
    private String format;
    private boolean timestamp;

    public DateHandlerContainer format(String format) {
        this.format = format;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public DateHandlerContainer timestamp(boolean use) {
        this.timestamp = use;
        return this;
    }

    public DateHandlerContainer timestamp() {
        return timestamp(true);
    }

    public boolean isTimestamp() {
        return timestamp;
    }
}
