package me.tr.trserializer.handlers.dates;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHandler extends DateHandlerContainer {

    @Override
    public Date deserialize(Object obj, GenericType<?> type) {

        if ((isTimestamp() || Number.class.isAssignableFrom(type.getTypeClass()))
                && obj instanceof Number num) {
            return new Date(num.longValue());
        }

        if (obj instanceof String date) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(getFormat());
                return formatter.parse(date);
            } catch (ParseException e) {
                TrLogger.exception(
                        new IllegalArgumentException(
                                "An error occurs while parsing " + date + " with format " + getFormat(), e
                        )
                );
                return null;
            }
        }

        TrLogger.exception(
                new TypeMissMatched(
                        "The provided object (" + Utility.getClassName(obj.getClass()) +
                                ") is not a valid type to deserialize it as Date."
                )
        );
        return null;
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Date date) {
            if (isTimestamp()) {
                return date.getTime();
            }
            SimpleDateFormat formatter = new SimpleDateFormat(getFormat());
            return formatter.format(date);
        }

        return obj;
    }
}
