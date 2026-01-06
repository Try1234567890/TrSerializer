package me.tr.trserializer;
import me.tr.trserializer.annotations.includeIf.IncludeIf;
import me.tr.trserializer.annotations.includeIf.IncludeStrategy;
import me.tr.trserializer.processes.serializer.Serializer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IncludeIfIntegrationTest {

    private final Serializer serializer = new Serializer();

    static class NotNullModel {
        @IncludeIf(strategy = IncludeStrategy.NOT_NULL)
        String value;

        NotNullModel(String value) {
            this.value = value;
        }
    }

    static class NotEmptyStringModel {
        @IncludeIf(strategy = IncludeStrategy.NOT_EMPTY)
        String value;

        NotEmptyStringModel(String value) {
            this.value = value;
        }
    }

    static class NotEmptyCollectionModel {
        @IncludeIf(strategy = IncludeStrategy.NOT_EMPTY)
        List<String> values;

        NotEmptyCollectionModel(List<String> values) {
            this.values = values;
        }
    }

    static class NotEmptyOptionalModel {
        @IncludeIf(strategy = IncludeStrategy.NOT_EMPTY)
        Optional<String> value;

        NotEmptyOptionalModel(Optional<String> value) {
            this.value = value;
        }
    }

    static class NotEmptyArrayModel {
        @IncludeIf(strategy = IncludeStrategy.NOT_EMPTY)
        int[] values;

        NotEmptyArrayModel(int[] values) {
            this.values = values;
        }
    }

    /* ===================== NOT_NULL ===================== */

    @Test
    void notNull_includedWhenValueIsNotNull() {
        Object serialized = serializer.serialize(new NotNullModel("ok"));

        assertTrue(serialized.toString().contains("value"));
    }

    @Test
    void notNull_excludedWhenValueIsNull() {
        Object serialized = serializer.serialize(new NotNullModel(null));

        System.out.println(serialized.toString());
        assertFalse(serialized.toString().contains("value"));
    }

    /* ===================== NOT_EMPTY – String ===================== */

    @Test
    void notEmptyString_includedWhenNotEmpty() {
        Object serialized = serializer.serialize(new NotEmptyStringModel("abc"));

        assertTrue(serialized.toString().contains("value"));
    }

    @Test
    void notEmptyString_excludedWhenEmpty() {
        Object serialized = serializer.serialize(new NotEmptyStringModel(""));

        System.out.println(serialized.toString());
        assertFalse(serialized.toString().contains("value"));
    }

    /* ===================== NOT_EMPTY – Collection ===================== */

    @Test
    void notEmptyCollection_includedWhenHasElements() {
        Object serialized = serializer.serialize(
                new NotEmptyCollectionModel(List.of("x"))
        );

        assertTrue(serialized.toString().contains("values"));
    }

    @Test
    void notEmptyCollection_excludedWhenEmpty() {
        Object serialized = serializer.serialize(
                new NotEmptyCollectionModel(List.of())
        );

        System.out.println(serialized.toString());
        assertFalse(serialized.toString().contains("values"));
    }

    /* ===================== NOT_EMPTY – Optional ===================== */

    @Test
    void notEmptyOptional_includedWhenPresent() {
        Object serialized = serializer.serialize(
                new NotEmptyOptionalModel(Optional.of("ok"))
        );

        assertTrue(serialized.toString().contains("value"));
    }

    @Test
    void notEmptyOptional_excludedWhenEmpty() {
        Object serialized = serializer.serialize(
                new NotEmptyOptionalModel(Optional.empty())
        );

        assertFalse(serialized.toString().contains("value"));
    }

    /* ===================== NOT_EMPTY – Array ===================== */

    @Test
    void notEmptyArray_includedWhenLengthGreaterThanZero() {
        Object serialized = serializer.serialize(
                new NotEmptyArrayModel(new int[]{1, 2})
        );

        assertTrue(serialized.toString().contains("values"));
    }

    @Test
    void notEmptyArray_excludedWhenEmpty() {
        Object serialized = serializer.serialize(
                new NotEmptyArrayModel(new int[]{})
        );

        System.out.println(serialized.toString());
        assertFalse(serialized.toString().contains("values"));
    }
}

