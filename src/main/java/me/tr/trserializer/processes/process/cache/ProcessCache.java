package me.tr.trserializer.processes.process.cache;

import me.tr.trserializer.processes.process.Process;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProcessCache extends Cache<Object, Object> {
    private final FieldsCache fieldsCache;
    private final InstancesCache instancesCache;
    private final MethodsCache methodsCache;

    public ProcessCache(Process process, Map<Object, Object> cache) {
        super(process, cache);
        this.fieldsCache = new FieldsCache(process);
        this.instancesCache = new InstancesCache(process);
        this.methodsCache = new MethodsCache(process);
    }

    public FieldsCache getFieldsCache() {
        return fieldsCache;
    }

    public InstancesCache getInstancesCache() {
        return instancesCache;
    }

    public MethodsCache getMethodsCache() {
        return methodsCache;
    }

    public static class MethodsCache extends Cache<Class<?>, Method[]> {

        public MethodsCache(Process process) {
            super(process, new HashMap<>());
        }
    }

    public static class FieldsCache extends Cache<Class<?>, Set<Field>> {

        public FieldsCache(Process process) {
            super(process, new HashMap<>());
        }
    }

    public static class InstancesCache extends Cache<Class<?>, Object> {

        public InstancesCache(Process process) {
            super(process, new HashMap<>());
        }
    }
}
