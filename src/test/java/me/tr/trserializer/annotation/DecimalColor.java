package me.tr.trserializer.annotation;

import me.tr.trserializer.annotations.AsNumber;

@AsNumber
public record DecimalColor(int decimal) {

    @Override
    public String toString() {
        return "DecimalColor{" +
                "decimal=" + decimal +
                '}';
    }
}
