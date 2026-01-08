package me.tr.trserializer.processes.process;

import me.tr.trlogger.loggers.TrConsoleLogger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.IdentityHashMap;
import java.util.Map;

public class ProcessLogger extends TrConsoleLogger {
    private static final Map<Process, ProcessLogger> LOGGERS = new IdentityHashMap<>();
    private final Process process;

    public ProcessLogger(Process process) {
        if ()
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    public void exception(Throwable throwable) {
        error(getStackTraceAsString(throwable));
    }

    public String getStackTraceAsString(Throwable throwable) {
        if (!getProcess().getOptions().isExpandedExceptions()) {
            return throwable.getMessage();
        }

        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
        }
        return sw.toString();
    }


}
