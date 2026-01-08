package me.tr.trserializer.person;

import me.tr.trserializer.annotations.Format;
import me.tr.trserializer.annotations.Getter;
import me.tr.trserializer.annotations.Initialize;
import me.tr.trserializer.annotations.unwrap.Unwrap;
import me.tr.trserializer.annotations.naming.Naming;
import me.tr.trserializer.annotations.naming.NamingStrategy;

import java.time.LocalDate;

@Naming(strategy = NamingStrategy.PASCAL_CASE)
public class Birthday {
    @Unwrap
    @Format(format = "dd.MM.yyyy")
    private LocalDate date;
    private int age;

    @Initialize(paramNames = {"date"})
    public Birthday(LocalDate date) {
        this.date = date;
        this.age = LocalDate.now().getYear() - (date == null ? 0 : date.getYear());
    }

    public LocalDate getDate() {
        if (date == null)
            return date = LocalDate.now();
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Birthday birthday) {
            return getDate().equals(birthday.getDate());
        }

        return false;
    }

    @Override
    public String toString() {
        return "Birthday{" +
                "date=" + date +
                ", age=" + age +
                '}';
    }
}
