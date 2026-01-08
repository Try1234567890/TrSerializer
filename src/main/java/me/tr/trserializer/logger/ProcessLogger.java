package me.tr.trserializer.logger;

import me.tr.trlogger.levels.TrLevel;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.utility.Utility;

import java.util.IdentityHashMap;
import java.util.Map;

public class ProcessLogger extends Logger {
    private static final Map<Process, ProcessLogger> LOGGERS = new IdentityHashMap<>();
    private final Process process;

    private ProcessLogger(Process process) {
        this.process = process;
        LOGGERS.put(process, this);
    }

    public static ProcessLogger of(Process process) {
        if (LOGGERS.containsKey(process)) {
            return LOGGERS.get(process);
        }
        return new ProcessLogger(process);
    }

    public Process getProcess() {
        return process;
    }

    public void throwable(Throwable throwable) {
        error(getStackTraceAsString(throwable));
    }

    @Override
    protected String compose(String msg, TrLevel level) {
        return "[" + Utility.getClassName(getProcess().getClass()) + "] " + super.compose(msg, level);
    }
}
