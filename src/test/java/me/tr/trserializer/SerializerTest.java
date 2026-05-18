package me.tr.trserializer;

import me.tr.trserializer.deserializer.iterative.IDeserializer;
import me.tr.trserializer.serializer.iterative.ISerializer;
import me.tr.trserializer.utility.SLogger;
import me.tr.trserializer.utility.Utility;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class SerializerTest {
    @Test
    public void testComplexSerialization() {
        //SLogger.LOGGER.setDebug(true);
        ISerializer serializer = new ISerializer();
        IDeserializer deserializer = new IDeserializer();

        // 1. Prepariamo i dati interni (Employee)
        Employee emp1 = new Employee(
                "Mario Rossi",
                new Date(),
                Optional.of("Luigi"),
                new AtomicInteger(42)
        );
        Employee emp2 = new Employee(
                "Anna Bianchi",
                new Date(),
                Optional.empty(),
                new AtomicInteger(100)
        );

        // 2. Prepariamo le collezioni e matrici
        List<Employee> employees = List.of(emp1, emp2);
        Map<String, Role> roleMap = Map.of(
                "Mario Rossi", Role.ADMIN,
                "Anna Bianchi", Role.USER
        );
        double[][] matrix = {
                {1.5, 2.3},
                {4.0, 5.5}
        };
        char[][][] recipes = {
                {
                        {'A', 'V', 'V', 'A'},
                        {'D', 'A', 'A', 'D'},
                        {'C', 'C', 'C', 'C'}
                },
                {
                        {'A', 'V', 'V', 'A'},
                        {'D', 'A', 'A', 'D'},
                        {'C', 'C', 'C', 'C'}
                },
                {
                        {'A', 'V', 'V', 'A'},
                        {'D', 'A', 'A', 'D'},
                        {'C', 'C', 'C', 'C'}
                }
        };

        // 3. Creiamo l'oggetto principale (Company)
        Company company = new Company("TechCorp", employees, roleMap, matrix, recipes);

        System.out.println("--- ORIGINALE ---");
        System.out.println(company);

        // 4. Serializzazione
        Map<String, Object> serializedData = serializer.serialize(company);
        System.out.println("\n--- SERIALIZZATO (Map) ---");
        System.out.println(serializedData);

        System.out.println("====== ISPEZIONE TIPI DELLA MAPPA SERIALIZZATA ======");
        System.out.println(dumpMapTypes(serializedData));
        System.out.println("=====================================================");

        // 5. Deserializzazione
        Company deserializedCompany = deserializer.deserialize(serializedData, Company.class);
        System.out.println("\n--- DESERIALIZZATO ---");
        System.out.println(deserializedCompany);

        // Qui puoi aggiungere i tuoi assert di JUnit per verificare la correttezza
    }

    // 1. Un Enum per testare la gestione delle costanti
    public enum Role {
        ADMIN, USER, GUEST
    }

    // 2. Una classe che contiene collezioni, array, enum e tipi speciali
    public static class Company {
        private final String name;
        private final List<Employee> employees; // Collezioni di POJO
        private final Map<String, Role> roleMapping; // Mappe con Enum
        private final double[][] financialMatrix; // Double array (multidimensionale)
        private final char[][][] recipes; // Char array (tridimensionale)

        public Company(String name, List<Employee> employees, Map<String, Role> roleMapping, double[][] financialMatrix, char[][][] recipes) {
            this.name = name;
            this.employees = employees;
            this.roleMapping = roleMapping;
            this.financialMatrix = financialMatrix;
            this.recipes = recipes;
        }

        public String getName() {
            return name;
        }

        public List<Employee> getEmployees() {
            return employees;
        }

        public Map<String, Role> getRoleMapping() {
            return roleMapping;
        }

        public double[][] getFinancialMatrix() {
            return financialMatrix;
        }

        public char[][][] getRecipes() {
            return recipes;
        }

        @Override
        public String toString() {
            return "Company{" +
                    "name='" + name + '\'' +
                    ", employees=" + employees +
                    ", roleMapping=" + roleMapping +
                    ", financialMatrix=" + java.util.Arrays.deepToString(financialMatrix) +
                    ", recipes=" + java.util.Arrays.deepToString(recipes) +
                    '}';
        }
    }

    // 3. Una classe dipendente con Optional, Date e Atomic
    public static class Employee {
        private final String fullName;
        private final Date hireDate; // Date
        private final Optional<String> middleName; // Optional
        private final AtomicInteger loginCounter; // Atomic

        public Employee(String fullName, Date hireDate, Optional<String> middleName, AtomicInteger loginCounter) {
            this.fullName = fullName;
            this.hireDate = hireDate;
            this.middleName = middleName;
            this.loginCounter = loginCounter;
        }

        public String getFullName() {
            return fullName;
        }

        public Date getHireDate() {
            return hireDate;
        }

        public Optional<String> getMiddleName() {
            return middleName;
        }

        public AtomicInteger getLoginCounter() {
            return loginCounter;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "fullName='" + fullName + '\'' +
                    ", hireDate=" + hireDate +
                    ", middleName=" + middleName.orElse("None") +
                    ", loginCounter=" + (loginCounter != null ? loginCounter.get() : "null") +
                    '}';
        }
    }

    public static String dumpMapTypes(Object obj) {
        return dumpMapTypesRecursive(obj, 0);
    }

    private static String dumpMapTypesRecursive(Object obj, int indent) {
        String indentation = "  ".repeat(indent);
        StringBuilder sb = new StringBuilder();

        if (obj == null) {
            return "null\n";
        }

        if (obj instanceof Map<?, ?> map) {
            sb.append(Utility.getClassName(obj)).append(" {\n");
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                sb.append(indentation).append("  \"").append(entry.getKey()).append("\"");
                // Mostriamo anche il tipo della chiave se non è una String standard
                if (!(entry.getKey() instanceof String)) {
                    sb.append(" (").append(entry.getKey().getClass().getName()).append(")");
                }
                sb.append(" -> ");
                sb.append(dumpMapTypesRecursive(entry.getValue(), indent + 1));
            }
            sb.append(indentation).append("}\n");
        } else if (obj instanceof List<?> list) {
            sb.append(Utility.getClassName(obj)).append(" [\n");
            for (int i = 0; i < list.size(); i++) {
                sb.append(indentation).append("  [").append(i).append("] -> ");
                sb.append(dumpMapTypesRecursive(list.get(i), indent + 1));
            }
            sb.append(indentation).append("]\n");
        } else if (obj.getClass().isArray()) {
            sb.append(Utility.getClassName(obj)).append(" [\n");
            int length = java.lang.reflect.Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                Object element = java.lang.reflect.Array.get(obj, i);
                sb.append(indentation).append("  [").append(i).append("] -> ");
                sb.append(dumpMapTypesRecursive(element, indent + 1));
            }
            sb.append(indentation).append("]\n");
        }

        sb.append(obj)
                .append(" (")
                .append(Utility.getClassName(obj))
                .append(")\n");

        return sb.toString();
    }
}
