package me.tr.trserializer.person;

import java.util.Objects;

public class Pet {
    private Generalities generalities;
    private Gender gender;
    private int age;
    private String type;


    public Pet(Generalities generalities, int age, Gender gender, String type) {
        this.generalities = generalities;
        this.age = age;
        this.gender = gender;
        this.type = type;
    }

    public Generalities getGeneralities() {
        return generalities;
    }

    public Pet setGeneralities(Generalities generalities) {
        this.generalities = generalities;
        return this;
    }

    public int getAge() {
        return age;
    }

    public Pet setAge(int age) {
        this.age = age;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public Pet setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public String getType() {
        return type;
    }

    public Pet setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof Pet pet) {
            return getAge() == pet.getAge() &&
                    Objects.equals(getGeneralities(), pet.getGeneralities()) &&
                    getGender() == pet.getGender() &&
                    Objects.equals(getType(), pet.getType());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGeneralities(), getGender(), getAge(), getType());
    }

    @Override
    public String toString() {
        return "Pet{" +
                "generalities=" + generalities +
                ", gender=" + gender +
                ", age=" + age +
                ", type='" + type + '\'' +
                '}';
    }
}
