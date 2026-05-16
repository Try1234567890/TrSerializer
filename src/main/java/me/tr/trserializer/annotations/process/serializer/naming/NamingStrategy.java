package me.tr.trserializer.annotations.process.serializer.naming;


import me.tr.trformatter.strings.CString;
import me.tr.trformatter.strings.cases.CaseType;
import me.tr.trserializer.utility.SLogger;

public enum NamingStrategy {

    CAMEL_CASE(CaseType.CAMEL) {
        @Override
        public String format(CString str) {
            return str.toCamelCase().getText();
        }
    },

    DOT_CASE(CaseType.DOT) {
        @Override
        public String format(CString str) {
            return str.toDotCase().getText();
        }
    },
    PASCAL_CASE(CaseType.PASCAL) {
        @Override
        public String format(CString str) {
            return str.toPascalCase().getText();
        }
    },

    SNAKE_CASE(CaseType.SNAKE) {
        @Override
        public String format(CString str) {
            return str.toSnakeCase().getText();
        }
    },

    TRAIN_CASE(CaseType.TRAIN) {
        @Override
        public String format(CString str) {
            return str.toTrainCase().getText();
        }
    },

    NOTHING(null) {
        @Override
        public String format(CString str) {
            return str.getText();
        }
    };


    private final CaseType caseType;

    NamingStrategy(CaseType caseType) {
        this.caseType = caseType;
    }

    public CaseType getCaseType() {
        return caseType;
    }

    public abstract String format(CString str);

    public String format(String str) {
        String formatted = format(CString.of(str));
        SLogger.LOGGER.debug("Formatted " + str + " to " + formatted);
        return formatted;
    }
}
