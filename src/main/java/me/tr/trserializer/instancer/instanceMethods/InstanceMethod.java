package me.tr.trserializer.instancer.instanceMethods;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface InstanceMethod {

    <T> T apply(Class<T> cls, Map<String, Object> params) throws InvocationTargetException, InstantiationException, IllegalAccessException;

}
