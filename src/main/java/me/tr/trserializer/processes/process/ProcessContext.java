package me.tr.trserializer.processes.process;

import me.tr.trserializer.instancers.ProcessInstancer;
import me.tr.trserializer.processes.process.addons.PAddon;

import java.util.*;

public class ProcessContext {
    private final Process process;
    private final ProcessOptions options;
    private final ProcessCache cache;
    private final SortedSet<PAddon> addons;


    public ProcessContext(Process process, ProcessCache cache, ProcessOptions options) {
        this.process = process;
        this.options = options;
        this.cache = cache;
        this.addons = new TreeSet<>(Comparator.comparingInt(PAddon::getPriorityCode));
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

    public SortedSet<PAddon> getAddons() {
        return addons;
    }
}
