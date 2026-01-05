package me.tr.trserializer.instancers;

import java.lang.reflect.Constructor;

/**
 * Try to construct an instance of the provided class
 * by calling the first construct with null parameters.
 * <p>
 * If not exists or an error occurs
 * while the process it will crash.
 */
public class NullInstancer implements Instancer {
    private boolean failed;
    private Throwable reason;

    @Override
    public Object instance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[constructor.getParameterCount()]);
        } catch (Exception e) {
            setFailed();
            setReason(e);
            return null;
        }
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

    @Override
    public void reset() {
        this.failed = false;
        setReason(null);
    }
}
