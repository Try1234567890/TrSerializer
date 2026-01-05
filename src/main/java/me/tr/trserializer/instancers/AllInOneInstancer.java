package me.tr.trserializer.instancers;

import java.util.Arrays;
import java.util.HashMap;

public class AllInOneInstancer implements Instancer {
    private static final Instancer[] INSTANCERS = new Instancer[]{
            new EmptyInstancer(),
            new NullInstancer(),
            new ParamsInstancer(new HashMap<>())
    };

    private boolean failed;
    private Throwable reason;

    /**
     * Instance the class.
     *
     * @param clazz The class to instance.
     * @return The instance of the provided class if the process ends successfully, otherwise {@code null}.
     */
    @Override
    public Object instance(Class<?> clazz) {
        Instancer instancer = null;
        for (Instancer value : INSTANCERS) {
            instancer = value;
            Object val = instancer.instance(clazz);
            if (!instancer.isFailed()) {
                return val;
            }
        }
        setFailed();
        setReason(instancer.getReason());
        Arrays.stream(INSTANCERS).forEach(Instancer::reset);
        return null;
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
