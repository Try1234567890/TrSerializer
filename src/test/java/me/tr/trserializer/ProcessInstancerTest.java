package me.tr.trserializer;

import me.tr.trserializer.annotations.Initialize;
import me.tr.trserializer.instancers.ProcessInstancer;
import me.tr.trserializer.processes.deserializer.Deserializer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProcessInstancerTest {


    static class EmptyConstructorClass {
        public EmptyConstructorClass() {
        }
    }

    static class AnnotatedConstructorClass {
        final int value;

        @Initialize
        public AnnotatedConstructorClass(int value) {
            this.value = value;
        }
    }

    static class AnnotatedConstructorWithNames {
        final int a;
        final String b;

        @Initialize(paramNames = {"a", "b"})
        public AnnotatedConstructorWithNames(int a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    static class ForceNamesConstructor {
        final int x;

        @Initialize(forceNames = true)
        public ForceNamesConstructor(int x) {
            this.x = x;
        }
    }

    static class StaticFactoryMethodClass {
        final int value;

        private StaticFactoryMethodClass(int value) {
            this.value = value;
        }

        @Initialize
        public static StaticFactoryMethodClass create(int value) {
            return new StaticFactoryMethodClass(value);
        }
    }

    static class SingletonClass {
        final int value;

        @Initialize(isSingleton = true)
        public SingletonClass(int value) {
            this.value = value;
        }
    }

    static class NoAnnotationMultipleConstructors {
        public NoAnnotationMultipleConstructors(int a) {
        }

        public NoAnnotationMultipleConstructors() {
        }
    }

    static class DefaultValueClass {
        final int number;

        public DefaultValueClass(int number) {
            this.number = number;
        }
    }

    static class WrongReturnTypeMethod {
        @Initialize
        public static String build() {
            return "invalid";
        }
    }

    @Test
    void testAnnotatedConstructorInstantiation() {
        ProcessInstancer instancer = new ProcessInstancer(
                new Deserializer(),
                Map.of("value", 10)
        );

        Object obj = instancer.instance(AnnotatedConstructorClass.class);

        assertNotNull(obj);
        assertEquals(10, ((AnnotatedConstructorClass) obj).value);
    }

    @Test
    void testAnnotatedConstructorWithExplicitNames() {
        ProcessInstancer instancer = new ProcessInstancer(
                new Deserializer(),
                Map.of("a", 5, "b", "test")
        );

        AnnotatedConstructorWithNames obj =
                (AnnotatedConstructorWithNames) instancer.instance(AnnotatedConstructorWithNames.class);

        assertEquals(5, obj.a);
        assertEquals("test", obj.b);
    }

    @Test
    void testForceNamesResolution() {
        ProcessInstancer instancer = new ProcessInstancer(
                new Deserializer(),
                Map.of("x", 42)
        );

        ForceNamesConstructor obj =
                (ForceNamesConstructor) instancer.instance(ForceNamesConstructor.class);

        assertEquals(42, obj.x);
    }

    @Test
    void testStaticFactoryMethodInstantiation() {
        ProcessInstancer instancer = new ProcessInstancer(
                new Deserializer(),
                Map.of("value", 99)
        );

        StaticFactoryMethodClass obj =
                (StaticFactoryMethodClass) instancer.instance(StaticFactoryMethodClass.class);

        assertEquals(99, obj.value);
    }

    @Test
    void testEmptyConstructorFallback() {
        ProcessInstancer instancer = new ProcessInstancer(new Deserializer());

        Object obj = instancer.instance(EmptyConstructorClass.class);

        assertNotNull(obj);
        assertInstanceOf(EmptyConstructorClass.class, obj);
    }

    @Test
    void testFirstConstructorFallback() {
        ProcessInstancer instancer = new ProcessInstancer(new Deserializer());

        Object obj = instancer.instance(NoAnnotationMultipleConstructors.class);

        assertNotNull(obj);
        assertInstanceOf(NoAnnotationMultipleConstructors.class, obj);
    }

    @Test
    void testDefaultValueInjectionWhenParamMissing() {
        ProcessInstancer instancer = new ProcessInstancer(new Deserializer());

        DefaultValueClass obj = (DefaultValueClass) instancer.instance(DefaultValueClass.class);

        assertEquals(-1, obj.number);
    }

    @Test
    void testSingletonBehavior() {
        ProcessInstancer instancer = new ProcessInstancer(
                new Deserializer(),
                Map.of("value", 7)
        );

        Object first = instancer.instance(SingletonClass.class);
        Object second = instancer.instance(SingletonClass.class);

        assertSame(first, second);
    }

    @Test
    void testInvalidInitializeMethodSetsFailure() {
        ProcessInstancer instancer = new ProcessInstancer(new Deserializer());

        Object obj = instancer.instance(WrongReturnTypeMethod.class);

        System.out.println("!! ERROR EXCEPTED BUT OBJECT MUST NOT BE NULL !!");
        System.out.println("!! ERROR EXCEPTED BUT OBJECT MUST NOT BE NULL !!");
        System.out.println("!! ERROR EXCEPTED BUT OBJECT MUST NOT BE NULL !!");
        System.out.println("!! ERROR EXCEPTED BUT OBJECT MUST NOT BE NULL !!");
        System.out.println("!! ERROR EXCEPTED BUT OBJECT MUST NOT BE NULL !!");

        System.out.println("Object is null ? " + (obj == null));
        System.out.println("Object is instance of WrongReturnTypeMethod ? " + (obj instanceof WrongReturnTypeMethod));

        assertInstanceOf(WrongReturnTypeMethod.class, obj);
    }
}

