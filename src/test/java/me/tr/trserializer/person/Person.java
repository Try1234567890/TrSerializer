package me.tr.trserializer.person;

import me.tr.trserializer.annotations.Initialize;
import me.tr.trserializer.annotations.SerializeAs;
import me.tr.trserializer.annotations.Unwrapped;
import me.tr.trserializer.annotations.naming.Naming;
import me.tr.trserializer.annotations.naming.NamingStrategy;

import java.util.*;


@Naming(strategy = NamingStrategy.PASCAL_CASE)
public class Person {
    @Unwrapped(fields = {"name", "surname"})
    private Generalities generalities;
    private Gender gender;
    @Unwrapped
    private Birthday birthday;
    private String[] hobbies;
    private List<Pet> pets;
    private List<Person> friends;

    public Person(Generalities generalities, Birthday birthday, Gender gender, String[] hobbies, List<Pet> pets) {
        this.generalities = generalities;
        this.birthday = birthday;
        this.gender = gender;
        this.hobbies = hobbies;
        this.pets = pets;
        this.friends = new ArrayList<>();
    }

    public Person(Generalities generalities, Birthday birthday, Gender gender, String[] hobbies, List<Pet> pets,
                  List<Person> friends) {
        this.generalities = generalities;
        this.birthday = birthday;
        this.gender = gender;
        this.hobbies = hobbies;
        this.pets = pets;
        this.friends = friends;
    }

    @Initialize(paramNames = {"generalities", "birthday", "gender"})
    public Person(Generalities generalities, Birthday birthday, Gender gender) {
        this.generalities = generalities;
        this.birthday = birthday;
        this.gender = gender;
    }

    public Generalities getGeneralities() {
        return generalities;
    }

    public Person setGeneralities(Generalities generalities) {
        this.generalities = generalities;
        return this;
    }

    public Birthday getBirthday() {
        return birthday;
    }

    public Person setBirthday(Birthday birthday) {
        this.birthday = birthday;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public Person setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public String[] getHobbies() {
        return hobbies;
    }

    public Person setHobbies(String[] hobbies) {
        this.hobbies = hobbies;
        return this;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public Person setPets(List<Pet> pets) {
        this.pets = pets;
        return this;
    }

    public List<Person> getFriends() {
        return friends;
    }

    public Person setFriends(List<Person> friends) {
        this.friends = friends;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof Person person) {
            return getBirthday() == person.getBirthday() &&
                    Objects.equals(getGeneralities(), person.getGeneralities()) &&
                    getGender() == person.getGender() &&
                    Objects.deepEquals(getHobbies(), person.getHobbies()) &&
                    Objects.equals(getPets(), person.getPets()) &&
                    Objects.equals(getFriends(), person.getFriends());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGeneralities(),
                getBirthday(),
                getGender(),
                Arrays.hashCode(getHobbies()),
                getPets(),
                getFriends()
        );
    }

    @Override
    public String toString() {
        return "Person{" +
                "generalities=" + generalities +
                ", birthday=" + birthday +
                ", gender=" + gender +
                ", hobbies=" + Arrays.toString(hobbies) +
                ", pets=" + pets +
                ", friends=" + friends +
                '}';
    }
}
