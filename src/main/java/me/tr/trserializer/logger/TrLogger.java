package me.tr.trserializer.logger;

import me.tr.trlogger.loggers.TrConsoleLogger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TrLogger extends TrConsoleLogger {
    private static final TrLogger INSTANCE = new TrLogger();

    public static TrLogger getInstance() {
        return INSTANCE;
    }

    public static void exception(Throwable e) {
        getInstance().error(getStackTraceAsString(e));
    }

    public static void msg(String message) {
        getInstance().info(message);
    }

    public static void warning(String message) {
        getInstance().warn(message);
    }

    public static void err(String message) {
        getInstance().error(message);
    }

    public static void dbg(String message) {
        getInstance().debug(message);
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
        }
        return sw.toString();
    }


}
