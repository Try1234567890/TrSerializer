package me.tr.trserializer.handlers.dates;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.logger.Logger;
import me.tr.trserializer.logger.ProcessLogger;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateHandler extends DateHandlerContainer {

    @Override
    public LocalDate deserialize(Object obj, GenericType<?> type) {
        if ((isTimestamp() || Number.class.isAssignableFrom(type.getTypeClass())) && obj instanceof Number num) {
            return Instant
                    .ofEpochMilli(num.longValue())
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate();
        }

        if (obj instanceof String date) {
            try {
                return LocalDate.parse(date, DateTimeFormatter.ofPattern(getFormat()));
            } catch (DateTimeParseException e) {
                Logger.exception(new DateTimeException("An error occurs while parsing " + date + " with format " + getFormat(), e));
                return null;
            }
        }

        Logger.exception(new TypeMissMatched("The provided object (" + Utility.getClassName(obj.getClass()) + ") is not a valid type to deserialize it as LocalDate."));
        return null;
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof LocalDate date) {
            if (isTimestamp()) {
                Instant instant = date
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant();

                return Timestamp.from(instant);
            }
            return date.format(DateTimeFormatter.ofPattern(getFormat()));
        }
        return obj;
    }
}
