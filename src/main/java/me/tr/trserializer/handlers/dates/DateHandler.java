package me.tr.trserializer.handlers.dates;

import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.exceptions.TypeMissMatched;
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
                throw new ProcessError("An error occurs while parsing " + date + " with format " + getFormat(), e);
            }
        }

        throw new TypeMissMatched("The provided object (" + Utility.getClassName(obj.getClass()) + ") is not a valid type to deserialize it as Date.");
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

        throw new TypeMissMatched("The provided object (" + Utility.getClassName(obj.getClass()) + ") is not a date.");
    }
}
