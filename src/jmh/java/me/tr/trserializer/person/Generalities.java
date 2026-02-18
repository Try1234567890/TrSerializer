package me.tr.trserializer.person;

import me.tr.trserializer.annotations.Initialize;
import me.tr.trserializer.annotations.naming.Naming;
import me.tr.trserializer.annotations.naming.NamingStrategy;

import java.util.Objects;

@Naming(strategy = NamingStrategy.PASCAL_CASE)
public record Generalities(String name, String surname) {

    @Initialize(paramNames = {"name", "surname"})
    public Generalities {

    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof Generalities(String name1, String surname1)) {
            return Objects.equals(name(), name1) &&
                    Objects.equals(surname(), surname1);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name(), surname());
    }
}
