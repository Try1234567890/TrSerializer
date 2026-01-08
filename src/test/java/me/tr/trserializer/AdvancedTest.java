package me.tr.trserializer;

import me.tr.trserializer.nodes.Node;
import me.tr.trserializer.person.*;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.deserializer.IDeserializer;
import me.tr.trserializer.processes.serializer.ISerializer;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancedTest {

    public void runAll() {
        testObjectIdentity();
        testNullAndEmptyFields();
        testInheritanceAndPolymorphism();
    }

    @Test
    public void testObjectIdentity() {
        System.out.println("\n===----------=== OBJECT IDENTITY TEST ===----------===");
        Pet sharedPet = new Pet(new Generalities("Shared", "Pet"), Gender.MALE, new Birthday(LocalDate.of(2019, Month.AUGUST, 12)), "parrot");

        Person p1 = new Person(new Generalities("User", "One"), new Birthday(LocalDate.of(2006, Month.APRIL, 3)), Gender.MALE, new String[]{}, List.of(sharedPet));
        Person p2 = new Person(new Generalities("User", "Two"), new Birthday(LocalDate.of(2001, Month.MARCH, 20)), Gender.FEMALE, new String[]{}, List.of(sharedPet));

        List<Person> group = List.of(p1, p2);
        List<Map<String, Object>> s =
                new Serializer().serialize(group, new GenericType<>(List.class, Map.class));

        System.out.println(s);

        List<Person> d = new Deserializer().deserialize(s, new GenericType<>(List.class, Person.class));

        System.out.println(d);

        boolean sameInstance = (d.get(0).getPets().getFirst() == d.get(1).getPets().getFirst());

        System.out.println("Identity preserved: " + sameInstance);
        if (!sameInstance) {
            System.out.println("Warning: Objects were cloned instead of maintaining reference!");
        }
    }

    @Test
    public void testNullAndEmptyFields() {
        System.out.println("\n===----------=== NULL & EMPTY FIELDS TEST ===----------===");
        try {
            Person p = new Person(null, new Birthday(LocalDate.of(1996, Month.FEBRUARY, 1)), Gender.MALE, null, new ArrayList<>());

            Map<String, Object> s = new Serializer().serialize(p, new GenericType<>(Map.class, String.class));
            Person d = new Deserializer().deserialize(s, Person.class);

            System.out.println("Passed: " + (d.getGeneralities() == null ? "Null preserved" : "Null changed"));
        } catch (Exception e) {
            System.out.println("Failed: Crash with null/empty fields - " + e.getMessage());
        }
    }

    @Test
    public void testInheritanceAndPolymorphism() {
        System.out.println("\n===----------=== INHERITANCE TEST ===----------===");
        Pet p = new Pet(new Generalities("Rex", "Dog"), Gender.MALE, new Birthday(LocalDate.of(2023, Month.SEPTEMBER, 26)), "canine");

        Map<String, Object> s = new Serializer().serialize(p, new GenericType<>(Map.class, String.class));
        Pet d = new Deserializer().deserialize(s, Pet.class);

        System.out.println("Correct class: " + (d instanceof Pet));
    }

    @Test
    public void testDeepNesting() {
        System.out.println("\n===----------=== DEEP NESTING TEST ===----------===");
        int depth = 10_000_000;
        Node head = new Node(null);
        Node current = head;

        for (int i = 1; i < depth; i++) {
            Node next = new Node(null);
            current.setNext(next);
            current = next;
        }

        try {
            Object s = new ISerializer().serialize(head);
            System.out.println("Serializer passed deep nesting of " + depth + " levels.");
            try {
                Node d = new IDeserializer().deserialize(s, Node.class);
                System.out.println("Deserializer passed deep nesting of " + depth + " levels.");
            } catch (StackOverflowError e) {
                System.out.println("Deserializer failed Stack Overflow at depth " + depth);
            }
        } catch (StackOverflowError e) {
            System.out.println("Serializer failed Stack Overflow at depth " + depth);
        }
    }
}
