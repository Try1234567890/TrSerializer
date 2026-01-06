package me.tr.trserializer.annotations.naming;

import me.tr.trformatter.strings.format.TextFormat;
import me.tr.trformatter.strings.format.formats.*;

public enum NamingStrategy {

    CAMEL_CASE(new CamelCase("")) {
        @Override
        public String format(String val, TextFormat from) {
            return from.str(val).toCaseFrom(getFormat()).getResult();
        }
    },

    DOT_CASE(new DotCase("")) {
        @Override
        public String format(String val, TextFormat from) {
            return getFormat().str(val).toCaseFrom(from).getResult();
        }
    },

    PASCAL_CASE(new PascalCase("")) {
        @Override
        public String format(String val, TextFormat from) {
            return getFormat().str(val).toCaseFrom(from).getResult();
        }
    },

    SNAKE_CASE(new SnakeCase("")) {
        @Override
        public String format(String val, TextFormat from) {
            return getFormat().str(val).toCaseFrom(from).getResult();
        }
    },

    TRAIN_CASE(new TrainCase("")) {
        @Override
        public String format(String val, TextFormat from) {
            return getFormat().str(val).toCaseFrom(from).getResult();
        }
    },

    NOTHING(null) {
        @Override
        public String format(String val, TextFormat from) {
            return null;
        }
    };


    private final TextFormat format;

    NamingStrategy(TextFormat format) {
        this.format = format;
    }

    public TextFormat getFormat() {
        return format;
    }

    public abstract String format(String val, TextFormat from);

    public String format(String str) {
        return format(str, CAMEL_CASE.getFormat().str(str));
    }
}
