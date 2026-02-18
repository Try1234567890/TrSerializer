package me.tr.trserializer.annotations.naming;


import me.tr.trformatter.strings.CString;
import me.tr.trformatter.strings.cases.CaseType;

public enum NamingStrategy {

    CAMEL_CASE(CaseType.CAMEL) {
        @Override
        public String format(CString to) {
            return to.toCamelCase().getText();
        }
    },

    DOT_CASE(CaseType.DOT) {
        @Override
        public String format(CString to) {
            return to.toDotCase().getText();
        }
    },
    PASCAL_CASE(CaseType.PASCAL) {
        @Override
        public String format(CString to) {
            return to.toPascalCase().getText();
        }
    },

    SNAKE_CASE(CaseType.SNAKE) {
        @Override
        public String format(CString to) {
            return to.toSnakeCase().getText();
        }
    },

    TRAIN_CASE(CaseType.TRAIN) {
        @Override
        public String format(CString to) {
            return to.toTrainCase().getText();
        }
    },

    NOTHING(null) {
        @Override
        public String format(CString to) {
            return to.getText();
        }
    };


    private final CaseType caseType;

    NamingStrategy(CaseType caseType) {
        this.caseType = caseType;
    }

    public CaseType getCaseType() {
        return caseType;
    }

    public abstract String format(CString to);

    public String format(String to) {
        return format(CString.of(to));
    }
}
