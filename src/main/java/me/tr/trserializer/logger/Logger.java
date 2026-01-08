package me.tr.trserializer.logger;

import me.tr.trlogger.levels.TrLevel;
import me.tr.trlogger.loggers.TrConsoleLogger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger extends TrConsoleLogger {
    private static final Logger LOGGER = new Logger();
    private final LoggerOptions options;

    public Logger() {
        this.options = new LoggerOptions(this);
    }

    public LoggerOptions getOptions() {
        return options;
    }

    public static void send(String msg, TrLevel level) {
        LOGGER.log(msg, level);
    }

    public static void warning(String msg) {
        LOGGER.warn(msg);
    }

    public static void dbg(String msg) {
        LOGGER.debug(msg);
    }

    public static void exception(Throwable throwable) {
        LOGGER.error(LOGGER.getStackTraceAsString(throwable));
    }

    public String getStackTraceAsString(Throwable throwable) {

        if (getOptions().isExpandedExceptions()) {
            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                throwable.printStackTrace(pw);
            }
            return sw.toString();
        }

        return throwable.getMessage();
    }


}
