package me.tr.serializer.instancers;

import me.tr.serializer.processes.Process;
import me.tr.serializer.processes.ProcessOptions;

/**
 * Try to construct an instance of the provided class
 * by calling a construct with no parameters.
 * <p>
 * If not exists or an error occurs
 * while the process it will crash.
 */
public class ProcessInstancer implements Instancer {
    private final Process process;
    private boolean failed;
    private Throwable reason;

    public ProcessInstancer(Process process) {
        this.process = process;
    }

    @Override
    public Object instance(Class<?> clazz) {
        if (getOptions().hasInstance(clazz)) {
            return getOptions().getInstance(clazz);
        }

        Object instance;

        AllInOneInstancer instancer = new AllInOneInstancer();
        instance = instancer.instance(clazz);

        if (instance == null) {
            setFailed();
            setReason(instancer.getReason());
        }

        return instance;
    }

    public Process getProcess() {
        return process;
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    private void setFailed() {
        this.failed = true;
    }

    @Override
    public Throwable getReason() {
        return reason;
    }

    private void setReason(Throwable reason) {
        this.reason = reason;
    }

    private ProcessOptions getOptions() {
        return getProcess().getOptions();
    }

    @Override
    public void reset() {
        this.failed = false;
        setReason(null);
    }
}

