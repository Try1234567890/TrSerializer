package me.tr.trserializer.processes.process.helper;

import me.tr.trlogger.levels.TrDebug;
import me.tr.trlogger.levels.TrError;
import me.tr.trlogger.levels.TrLevel;
import me.tr.trserializer.annotations.Ignore;
import me.tr.trserializer.annotations.includeIf.IncludeIf;
import me.tr.trserializer.annotations.includeIf.IncludeStrategy;
import me.tr.trserializer.logger.Logger;
import me.tr.trserializer.processes.process.ProcessOptions;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

public class ProcessValidator {
    private final Process process;

    public ProcessValidator(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    public ProcessOptions getOptions() {
        return getProcess().getOptions();
    }

    /**
     * Determines if the package of the provided object is restricted by process options.
     */
    public ValidationResult isPackageBlocked(Object o) {
        if (o == null)
            return ValidationResult.fatal("The provided object is null.");


        if (getOptions().isPackageBlocked(o.getClass().getPackageName())) {
            return ValidationResult.error("The provided object package is blocked.");
        }

        return ValidationResult.success();
    }

    /**
     * Validates if an object and its generic type are eligible for processing.
     *
     * @param obj  the object to validate.
     * @param type the generic type metadata.
     * @return the {@link ValidationResult} of the check.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public ValidationResult isValid(Object obj, GenericType<?> type) {
        if (type == null)
            return ValidationResult.fatal("The provided object is null.");

        return isValid(obj);
    }

    /**
     * Validates if an object is eligible for processing based on nullability and process options.
     *
     * @param obj the object to validate.
     * @return a {@link ValidationResult} indicating success or the severity of the failure.
     */
    public ValidationResult isValid(Object obj) {
        if (obj == null) {
            if (!getOptions().isAcceptNulls())
                return ValidationResult.fatal("The provided object is null and the process not accept null values.");
            return ValidationResult.error("The provided object is null but the process accepts null values.");
        }


        if (obj instanceof Optional<?> opt && opt.isEmpty()) {
            if (!getOptions().isAcceptEmptyOptional())
                return ValidationResult.fatal("The provided object is an empty optional and the process not accept empty optional values.");
            return ValidationResult.error("The provided object is an empty optional but the process accepts empty optional values.");
        }

        return isPackageBlocked(obj);
    }

    /**
     * Validates if a field should be included in the process.
     * <p>
     * A field is considered invalid if it is annotated with {@link Ignore},
     * fails a {@link IncludeIf} strategy, or violates modifier-based rules
     * defined in {@link ProcessOptions}.
     * </p>
     *
     * @param field the field to validate.
     * @param value the current value of the field.
     * @return a {@link ValidationResult} representing the validation state.
     */
    public ValidationResult isValid(Field field, Object value) {
        if (field == null) {
            return ValidationResult.fatal("The provided field is null.");
        }

        String fieldName = field.getName();

        if (field.isAnnotationPresent(IncludeIf.class)) {
            IncludeIf ann = field.getAnnotation(IncludeIf.class);
            IncludeStrategy strategy = ann.strategy();


            if (strategy == null) {
                return ValidationResult.fatal("The strategy of @IncludeIf on " + fieldName + " in class " + Utility.getClassName(field.getDeclaringClass()) + " is null. Ignoring it.");
            }

            if (!strategy.isValid(value)) {
                return ValidationResult.error("The value of " + fieldName + " doesn't pass the @IncludeIf strategy check. Skipping it...");
            }
        }


        return isValid(field);
    }

    public ValidationResult isValid(Field field) {
        String fieldName = field.getName();

        int mod = field.getModifiers();
        if (field.isAnnotationPresent(Ignore.class)) {
            return ValidationResult.error("The field " + fieldName + " is annotated with @Ignore.");
        }
        if (getOptions().isIgnoreStatic() && Modifier.isStatic(mod)) {
            return ValidationResult.error("The option \"ignore static\" is enabled and " + fieldName + " is static");
        }
        if (getOptions().isIgnoreFinal() && Modifier.isFinal(mod)) {
            return ValidationResult.error("The option \"ignore final\" is enabled and " + fieldName + " is final");
        }
        if (getOptions().isIgnoreTransient() && Modifier.isTransient(mod)) {
            return ValidationResult.error("The option \"ignore transient\" is enabled and " + fieldName + " is transient");
        }

        return ValidationResult.success();
    }

    /**
     * Represents the outcome of a validation check, including success status and logging info.
     */
    public static class ValidationResult {
        private final boolean success;
        private final String message;
        private final TrLevel level;


        private ValidationResult(boolean success, String message, TrLevel level) {
            this.success = success;
            this.message = message;
            this.level = level;
        }

        /**
         * Returns a successful result with no message.
         */
        public static ValidationResult success() {
            return new ValidationResult(true, "", null);
        }

        /**
         * Returns a failed result that should be logged as an error.
         *
         * @param msg the error message.
         */
        public static ValidationResult fatal(String msg) {
            return new ValidationResult(false, msg, TrError.ERROR);
        }

        /**
         * Returns a failed result that should be logged as a debug message.
         *
         * @param msg the debug/info message.
         */
        public static ValidationResult error(String msg) {
            return new ValidationResult(false, msg, TrDebug.DEBUG);
        }

        /**
         * Prints the message to the logger if a log level is assigned.
         */
        public void print() {
            if (level != null)
                Logger.send(message(), level);
        }

        /**
         * Prints the result and returns the success status.
         *
         * @return {@code true} if validation passed.
         */
        public boolean isSuccess() {
            print();
            return success;
        }

        /**
         * @return the validation message.
         */
        public String message() {
            return message;
        }
    }
}
