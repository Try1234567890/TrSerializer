package me.tr.serializer;

import me.tr.serializer.nodes.Node;
import me.tr.serializer.person.Gender;
import me.tr.serializer.person.Generalities;
import me.tr.serializer.person.Person;
import me.tr.serializer.person.Pet;
import me.tr.serializer.processes.deserializer.Deserializer;
import me.tr.serializer.processes.serializer.Serializer;
import me.tr.serializer.types.GenericType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancedTests {

    public static void runAll() {
        testObjectIdentity();
        testNullAndEmptyFields();
        testInheritanceAndPolymorphism();
        testDeepNesting();
    }

    static void testObjectIdentity() {
        System.out.println("\n===----------=== OBJECT IDENTITY TEST ===----------===");
        Pet sharedPet = new Pet(new Generalities("Shared", "Pet"), 5, Gender.MALE, "parrot");

        Person p1 = new Person(new Generalities("User", "One"), 20, Gender.MALE, new String[]{}, List.of(sharedPet));
        Person p2 = new Person(new Generalities("User", "Two"), 25, Gender.FEMALE, new String[]{}, List.of(sharedPet));

        List<Person> group = List.of(p1, p2);
        Object s = new Serializer().serialize(group);

        System.out.println(s);

        List<Person> d = new Deserializer().deserialize(s, new GenericType<>(List.class, Person.class));

        System.out.println(d);

        boolean sameInstance = (d.get(0).getPets().getFirst() == d.get(1).getPets().getFirst());

        System.out.println("Identity preserved: " + sameInstance);
        if (!sameInstance) {
            System.out.println("Warning: Objects were cloned instead of maintaining reference!");
        }
    }


    static void testNullAndEmptyFields() {
        System.out.println("\n===----------=== NULL & EMPTY FIELDS TEST ===----------===");
        try {
            Person p = new Person(null, 30, Gender.MALE, null, new ArrayList<>());

            Map<String, Object> s = new Serializer().serialize(p, new GenericType<>(Map.class, String.class));
            Person d = new Deserializer().deserialize(s, Person.class);

            System.out.println("Passed: " + (d.getGeneralities() == null ? "Null preserved" : "Null changed"));
        } catch (Exception e) {
            System.out.println("Failed: Crash with null/empty fields - " + e.getMessage());
        }
    }

    static void testInheritanceAndPolymorphism() {
        System.out.println("\n===----------=== INHERITANCE TEST ===----------===");
        Pet p = new Pet(new Generalities("Rex", "Dog"), 3, Gender.MALE, "canine");

        Map<String, Object> s = new Serializer().serialize(p, new GenericType<>(Map.class, String.class));
        Pet d = new Deserializer().deserialize(s, Pet.class);

        System.out.println("Correct class: " + (d instanceof Pet));
    }

    static void testDeepNesting() {
        System.out.println("\n===----------=== DEEP NESTING TEST ===----------===");
        int depth = 2000;
        Node head = new Node("Node 0");
        Node current = head;

        for (int i = 1; i < depth; i++) {
            Node next = new Node("Node " + i);
            current.setNext(next);
            current = next;
        }

        try {
            Object s = new Serializer().serialize(head);
            System.out.println("Passed deep nesting of " + depth + " levels.");
        } catch (StackOverflowError e) {
            System.out.println("Failed: Stack Overflow at depth " + depth);
        }
    }
}
