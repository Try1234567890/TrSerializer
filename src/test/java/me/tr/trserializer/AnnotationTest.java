package me.tr.trserializer;

import me.tr.trserializer.annotation.DecimalColor;
import me.tr.trserializer.annotation.HexColor;
import me.tr.trserializer.person.Birthday;
import me.tr.trserializer.person.Gender;
import me.tr.trserializer.person.Generalities;
import me.tr.trserializer.person.Person;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.serializer.Serializer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class AnnotationTest {

    public void runAll() {
        testAsNumber();
        testAsString();
        testGetter();
    }

    @Test
    public void testSetter() {
        System.out.println("\n===----------=== SETTER ANNOTATION TEST ===----------===");

        Person person = new Person(
                null,
                new Birthday(LocalDate.of(2023, Month.SEPTEMBER, 26)),
                Gender.MALE,
                new String[0],
                List.of()
        );

        Object s = new Serializer().serialize(person);
        System.out.println(s);

        Person d = new Deserializer().deserialize(s, Person.class);
        System.out.println(d);
    }

    @Test
    public void testGetter() {
        System.out.println("\n===----------=== GETTER ANNOTATION TEST ===----------===");

        Birthday birthday = new Birthday(null);

        Object date = new Serializer().serialize(birthday);

        System.out.println(date);
    }

    @Test
    public void testAsString() {
        System.out.println("\n===----------=== AS STRING ANNOTATION TEST ===----------===");
        HexColor color = new HexColor("#000000");

        String s = new Serializer().serialize(color, String.class);
        System.out.println("serialized: " + s);

        HexColor d = new Deserializer().deserialize(s, HexColor.class);
        System.out.println("deserialized: " + d);
    }

    @Test
    public void testAsNumber() {
        System.out.println("\n===----------=== AS NUMBER ANNOTATION TEST ===----------===");
        DecimalColor color = new DecimalColor(255255255);

        int s = new Serializer().serialize(color, int.class);
        System.out.println("serialized: " + s);

        DecimalColor d = new Deserializer().deserialize(s, DecimalColor.class);
        System.out.println("deserialized: " + d);
    }

}
