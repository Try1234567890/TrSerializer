package me.tr.trserializer.logger;

import me.tr.trserializer.processes.options.Option;
import me.tr.trserializer.processes.options.Options;

public class LoggerOptions {
    private final Logger logger;
    // If this is false, the exception will be shortened in a single line.
    private final Option<Boolean> expandedExceptions = new Option<>(Options.EXPAND_EXCEPTION, false);

    public LoggerOptions(Logger logger) {
        this.logger = logger;
    }

    /*
     * =---------------------=
     * EXPANDED EXCEPTIONS OPTIONS
     * =---------------------=
     */

    public Option<Boolean> getExpandedExceptions() {
        return expandedExceptions;
    }

    public boolean isExpandedExceptions() {
        return getExpandedExceptions().getValue();
    }

    public LoggerOptions setExpandedExceptions(boolean expandedExceptions) {
        getExpandedExceptions().setValue(expandedExceptions);
        return this;
    }

    public Logger getLogger() {
        return logger;
    }
}
