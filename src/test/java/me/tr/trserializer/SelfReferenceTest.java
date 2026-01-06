package me.tr.trserializer;

import me.tr.trserializer.nodes.Node;
import me.tr.trserializer.person.Gender;
import me.tr.trserializer.person.Generalities;
import me.tr.trserializer.person.Person;
import me.tr.trserializer.person.Pet;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class SelfReferenceTest {

    public void runAll() {
        testSelfReferenceWithNodes();
        testSelfReferenceInMiddleWithNodes();
        testSelfReferenceWithPerson();
    }

    @Test
    public void testSelfReferenceWithNodes() {
        System.out.println("\n===----------=== SELF REFERENCE WITH NODES ===----------===");

        try {
            Node A = new Node("A");
            Node B = new Node("B");
            Node C = new Node("C");
            Node D = new Node("D");
            Node E = new Node("E");

            A.setNext(B);
            B.setNext(C);
            C.setNext(D);
            D.setNext(E);
            E.setNext(A);

            Map<String, Object> nodes = new Serializer().serialize(A, new GenericType<>(Map.class, String.class));
            // Cannot print in console, result checked with IntellJ IDEA Debugger
            System.out.println("Passed, No Stack Overflow Error!");
        } catch (StackOverflowError e) {
            System.out.println("Failed, Stack Overflow Error!");
        }
    }

    @Test
    public void testSelfReferenceInMiddleWithNodes() {
        System.out.println("\n===----------=== MIDDLE SELF REFERENCE WITH NODES ===----------===");
        try {
            Node A = new Node("A");
            Node B = new Node("B");
            Node C = new Node("C");
            Node D = new Node("D");
            Node E = new Node("E");

            A.setNext(B);
            B.setNext(C);
            C.setNext(D);
            D.setNext(A);
            E.setNext(D);

            // E -> D -> A -> B -> C -> D -> A ¬
            //           |---------------------|

            Map<String, Object> s = new Serializer().serialize(E, new GenericType<>(Map.class, String.class));

            System.out.println("Passed, No Stack Overflow Error!");
        } catch (StackOverflowError e) {
            System.out.println("Failed, Stack Overflow Error!");
        }
    }

    @Test
    public void testSelfReferenceWithPerson() {
        System.out.println("\n===----------=== SELF REFERENCE WITH PERSON ===----------===");

        try {
            Person jonh = new Person(
                    null, //new Generalities("Jonh", "Smith"),
                    48, //48,
                    null, //Gender.MALE,
                    null, //new String[]{"math", "fisics"},
                    null //List.of(new Pet(new Generalities("Rudy", "Smith"), 7, Gender.MALE, "dog"))
            );

            jonh.setFriends(List.of(jonh));

            Map<String, Object> person = new Serializer().serialize(jonh, new GenericType<>(Map.class, String.class));
            // Cannot print in console, result checked with IntellJ IDEA Debugger
            System.out.println("Passed, No Stack Overflow Error!");
        } catch (StackOverflowError e) {
            System.out.println("Failed, Stack Overflow Error!");
        }
    }

}
