package me.tr.trserializer.annotation;

import me.tr.trserializer.annotations.AsString;

@AsString
public record HexColor(String hex) {

    @Override
    public String toString() {
        return "HexColor{" +
                "hex='" + hex + '\'' +
                '}';
    }
}