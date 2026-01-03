package me.tr.serializer;


import me.tr.serializer.person.Gender;
import me.tr.serializer.person.Generalities;
import me.tr.serializer.person.Person;
import me.tr.serializer.person.Pet;
import me.tr.serializer.processes.deserializer.Deserializer;
import me.tr.serializer.processes.serializer.Serializer;
import me.tr.serializer.types.GenericType;
import me.tr.serializer.utility.Utility;

import java.util.*;

public class TrSerializer {

    public static void main(String[] args) {
        System.out.println(Utility.isWrapper(Integer.class));


        testBasics();
        testObjectIdentity();
        testGenericCollections();
        testPerformance();
        SelfReferenceTests.runAll();
        AdvancedTests.runAll();
    }

    static void testPerformance() {
        System.out.println("\n===----------=== PERFORMANCE TEST ===----------===");

        List<Person> largeList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeList.add(new Person(new Generalities("Name" + i, "Surname"), i, Gender.MALE, new String[]{"h1"}, new ArrayList<>()));
        }

        long start = System.currentTimeMillis();
        List<Person> s =
                new Serializer().serialize(largeList, new GenericType<>(List.class, Person.class));
        long end = System.currentTimeMillis();

        System.out.println("Serialized 10.000 objects in: " + (end - start) + "ms");
    }

    static void testGenericCollections() {
        System.out.println("\n===----------=== GENERICS & COLLECTIONS TEST ===----------===");
        Map<String, List<Integer>> complexMap = new HashMap<>();
        complexMap.put("numbers", Arrays.asList(1, 2, 3, 4, 5));

        Object s = new Serializer().serialize(complexMap);
        Map<String, List<Integer>> d = new Deserializer().deserialize(s,
                new GenericType<>(Map.class, String.class, List.class));

        Object firstElement = d.get("numbers").getFirst();
        System.out.println("Element type: " + firstElement.getClass().getSimpleName());

        if (firstElement instanceof Integer) {
            System.out.println("Passed: Integer type preserved.");
        } else {
            System.out.println("Failed: Integer became " + firstElement.getClass().getSimpleName());
        }
    }

    static void testObjectIdentity() {
        System.out.println("\n===----------=== OBJECT IDENTITY TEST ===----------===");

        Pet sharedPet = new Pet(new Generalities("Common", "Pet"), 5, Gender.MALE, "parrot");

        Person p1 = new Person(new Generalities("User", "One"), 20, Gender.MALE, new String[0], List.of(sharedPet));
        Person p2 = new Person(new Generalities("User", "Two"), 25, Gender.FEMALE, new String[0], List.of(sharedPet));

        List<Person> group = List.of(p1, p2);

        List<Map<String, Object>> serialized = new Serializer().serialize(group, new GenericType<>(List.class, Map.class));
        System.out.println("Serialized: " + serialized);

        List<Person> deserializedGroup = new Deserializer().deserialize(serialized, new GenericType<>(List.class, Person.class));
        System.out.println("DeserializedGroup: " + deserializedGroup.stream().map(Object::toString).toList());


        Person dp1 = deserializedGroup.get(0);
        Person dp2 = deserializedGroup.get(1);

        boolean sameObject = (dp1.getPets().getFirst() == dp2.getPets().getFirst());

        if (sameObject) {
            System.out.println("Passed: L'identità degli oggetti è stata preservata.");
        } else {
            System.out.println("Failed: Gli oggetti sono stati clonati invece di essere referenziati.");
        }
    }

    static void testBasics() {
        System.out.println("\n===----------=== BASICS ===----------===");

        Person person = new Person(
                new Generalities("Mario", "Rossi"),
                28,
                Gender.MALE,
                new String[]{"sport", "football"},
                List.of(
                        new Pet(new Generalities("Black", "Rossi"), 12, Gender.MALE, "cat"),
                        new Pet(new Generalities("White", "Rossi"), 12, Gender.FEMALE, "cat")
                ),
                List.of(
                        new Person(
                                new Generalities("White", "Red"),
                                10,
                                Gender.FEMALE,
                                new String[]{"dolls"},
                                new ArrayList<>()
                        ),
                        new Person(
                                new Generalities("Red", "Blue"),
                                29,
                                Gender.MALE,
                                new String[]{"basket"},
                                List.of(
                                        new Pet(new Generalities("Black", "Rossi"), 12, Gender.MALE, "cat"),
                                        new Pet(new Generalities("White", "Rossi"), 12, Gender.FEMALE, "cat")
                                )
                        )
                )
        );

        Map<String, Object> s = new Serializer().serialize(person, new GenericType<>(Map.class, String.class));

        System.out.println(s);

        Person d = new Deserializer().deserialize(s, Person.class);

        System.out.println(d);
    }


}