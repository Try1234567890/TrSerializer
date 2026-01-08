package me.tr.trserializer.processes.process;

import me.tr.trserializer.instancers.ProcessInstancer;
import me.tr.trserializer.logger.ProcessLogger;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.processes.process.helper.MethodsExecutor;
import me.tr.trserializer.processes.process.helper.NamingStrategyApplier;
import me.tr.trserializer.processes.process.helper.ProcessValidator;

import java.util.*;

public class ProcessContext {
    private final Process process;
    private final ProcessOptions options;
    private final ProcessCache cache;
    private final ProcessLogger logger;
    private final NamingStrategyApplier namingStrategyApplier;
    private final ProcessValidator processValidator;
    private final MethodsExecutor methodsExecutor;
    private final PriorityQueue<PAddon> addons;


    public ProcessContext(Process process, ProcessCache cache, ProcessOptions options) {
        this.process = process;
        this.options = options;
        this.cache = cache;
        this.logger = ProcessLogger.of(process);
        this.namingStrategyApplier = new NamingStrategyApplier(process);
        this.processValidator = new ProcessValidator(process);
        this.methodsExecutor = new MethodsExecutor(process);
        this.addons = new PriorityQueue<>(Comparator.comparingInt(PAddon::getPriorityCode));
    }

    public Process getProcess() {
        return process;
    }

    public ProcessOptions getOptions() {
        return options;
    }

    public ProcessCache getCache() {
        return cache;
    }

    public ProcessInstancer getInstancer(Map<String, Object> params) {
        return new ProcessInstancer(process, params);
    }

    public ProcessLogger getLogger() {
        return logger;
    }

    public NamingStrategyApplier getNamingStrategyApplier() {
        return namingStrategyApplier;
    }

    public ProcessValidator getProcessValidator() {
        return processValidator;
    }

    public MethodsExecutor getMethodsExecutor() {
        return methodsExecutor;
    }

    public PriorityQueue<PAddon> getAddons() {
        return addons;
    }
}
