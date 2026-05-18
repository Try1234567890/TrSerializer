package me.tr.trserializer.utility;

import me.tr.trlogger.loggers.StreamLogger;

public class SLogger {
    public static StreamLogger LOGGER = new StreamLogger();

    private SLogger() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility classes.");
    }
}
