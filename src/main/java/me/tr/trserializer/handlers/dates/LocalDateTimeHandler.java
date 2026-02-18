package me.tr.trserializer.handlers.dates;

import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeHandler extends DateHandlerContainer {

    @Override
    public LocalDateTime deserialize(Object obj, GenericType<?> type) {
        if ((isTimestamp() || Number.class.isAssignableFrom(type.getTypeClass()))
                && obj instanceof Number num) {
            return Instant
                    .ofEpochMilli(num.longValue())
                    .atZone(ZoneOffset.UTC)
                    .toLocalDateTime();
        }

        if (obj instanceof String date) {
            try {
                return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(getFormat()));
            } catch (DateTimeParseException e) {
                throw new ProcessError("An error occurs while parsing " + date + " with format " + getFormat(), e);
            }
        }

        throw new TypeMissMatched("The provided object (" + Utility.getClassName(obj.getClass()) + ") is not a valid type to deserialize it as LocalDateTime.");
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof LocalDateTime date) {
            if (isTimestamp()) {
                return Timestamp.from(date
                        .atZone(ZoneOffset.UTC)
                        .toInstant()
                );
            }
            return date.format(DateTimeFormatter.ofPattern(getFormat()));
        }

        throw new TypeMissMatched("The provided object (" + Utility.getClassName(obj.getClass()) + ") is not a LocalDateTime.");
    }
}
