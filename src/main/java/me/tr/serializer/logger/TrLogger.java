package me.tr.serializer.logger;

import me.tr.trlogger.loggers.TrConsoleLogger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TrLogger extends TrConsoleLogger {
    public static final TrLogger INSTANCE = new TrLogger();

    public static TrLogger getInstance() {
        return INSTANCE;
    }

    public void exception(Throwable e) {
        error(getStackTraceAsString(e));
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }


}
