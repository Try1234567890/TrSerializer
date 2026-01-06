package me.tr.trserializer;

import me.tr.trserializer.annotations.AsBoolean;
import me.tr.trserializer.handlers.annotation.AsBooleanHandler;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AsBooleanHandlerTest {

    private AsBooleanHandler handler;

    @BeforeEach
    void setup() {
        Process process = new Serializer();
        handler = new AsBooleanHandler(process);
    }

    @AsBoolean
    static class SingleFieldTrue {
        boolean value = true;
    }

    @AsBoolean
    static class SingleFieldFalse {
        boolean value = false;
    }

    @AsBoolean(field = "flag")
    static class MultipleFields {
        boolean flag = true;
        int other = 42;
    }

    @AsBoolean
    static class MultipleFieldsNoParam {
        boolean a = true;
        boolean b = false;
    }

    @AsBoolean(field = "missing")
    static class MissingField {
        boolean value = true;
    }

    static class NoAnnotation {
        boolean value = true;
    }

    @Test
    void testSingleFieldTrue() {
        Object result = handler.serialize(new SingleFieldTrue(), GenericType.of(Boolean.class));
        assertEquals(true, result);
    }

    @Test
    void testSingleFieldFalse() {
        Object result = handler.serialize(new SingleFieldFalse(), GenericType.of(Boolean.class));
        assertEquals(false, result);
    }

    @Test
    void testMultipleFieldsWithExplicitName() {
        Object result = handler.serialize(new MultipleFields(), GenericType.of(Boolean.class));
        assertEquals(true, result);
    }

    @Test
    void testMultipleFieldsWithoutParamReturnsNull() {
        Object result = handler.serialize(new MultipleFieldsNoParam(), GenericType.of(Boolean.class));
        assertNull(result);
    }

    @Test
    void testMissingFieldReturnsNull() {
        Object result = handler.serialize(new MissingField(), GenericType.of(Boolean.class));
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testNoAnnotationReturnsNull() {
        Object result = handler.serialize(new NoAnnotation(), GenericType.of(Boolean.class));
        assertNull(result);
    }
}
