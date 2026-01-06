package me.tr.trserializer.person;

import me.tr.trserializer.annotations.Initialize;

import java.util.Objects;

public class Pet {
    private Generalities generalities;
    private Gender gender;
    private Birthday birthday;
    private String type;

    @Initialize
    private Pet() {

    }

    public Pet(Generalities generalities, Gender gender, Birthday birthday, String type) {
        this.generalities = generalities;
        this.gender = gender;
        this.birthday = birthday;
        this.type = type;
    }

    public Generalities getGeneralities() {
        return generalities;
    }

    public Pet setGeneralities(Generalities generalities) {
        this.generalities = generalities;
        return this;
    }

    public Birthday getBirthday() {
        return birthday;
    }

    public Pet setBirthday(Birthday birthday) {
        this.birthday = birthday;
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
            return getBirthday() == pet.getBirthday() &&
                    Objects.equals(getGeneralities(), pet.getGeneralities()) &&
                    getGender() == pet.getGender() &&
                    Objects.equals(getType(), pet.getType());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGeneralities(), getGender(), getBirthday(), getType());
    }

    @Override
    public String toString() {
        return "Pet{" +
                "Generalities=" + generalities +
                ", Gender=" + gender +
                ", Birthday=" + birthday +
                ", Type='" + type + '\'' +
                '}';
    }
}
