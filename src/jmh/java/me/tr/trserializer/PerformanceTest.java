package me.tr.trserializer;

import me.tr.trserializer.person.Birthday;
import me.tr.trserializer.person.Gender;
import me.tr.trserializer.person.Generalities;
import me.tr.trserializer.person.Person;
import me.tr.trserializer.processes.serializer.ISerializer;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;
import org.openjdk.jmh.annotations.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PerformanceTest {
    private List<Person> data;
    private Serializer serializer;

    @Setup
    public void setup() {
        data = generateData(10_000);
        serializer = new Serializer();
    }

    @Benchmark
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 1)
    public List<Map<String, Object>> testSerialize() {
        return serializer.serialize(data, new GenericType<>(List.class, Map.class));
    }

    public List<Person> generateData(int size) {
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            persons.add(new Person(new Generalities("Name" + i, "Surname"), new Birthday(LocalDate.of(1900 + (i % 126), ((i % 12) + 1), ((i % 28) + 1))), Gender.MALE, new String[]{"h1"}, new ArrayList<>()));
        }
        return persons;
    }
}
