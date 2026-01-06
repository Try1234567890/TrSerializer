package me.tr.trserializer.handlers.dates;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.registries.HandlersRegistry;
import me.tr.trserializer.types.GenericType;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

public class SQLDateHandler extends DateHandlerContainer {
    private static final LocalDateHandler DATE_HANDLER = getLocalDateHandler();

    @Override
    public Date deserialize(Object obj, GenericType<?> type) {
        LocalDate date = DATE_HANDLER.deserialize(obj, type);

        if (date == null) {
            TrLogger.dbg("The LocalDateHandler returns null, stopping execution.");
            return null;
        }

        return Date.valueOf(date);
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Date date) {
            return DATE_HANDLER.serialize(date.toLocalDate(), type);
        }
        return obj;
    }

    private static LocalDateHandler getLocalDateHandler() {
        Optional<TypeHandler> optHandler = HandlersRegistry.getInstance().get(LocalDate.class, null);
        if (optHandler.isEmpty()) {
            TrLogger.err("The handler for LocalDate is not found. Creating a new instance of it.");
            return new LocalDateHandler();
        }

        TypeHandler handler = optHandler.get();
        if (!(handler instanceof LocalDateHandler dateHandler)) {
            TrLogger.err("The found handler for LocalDate is not instance of LocalDateHandler. Creating a new instance of it.");
            return new LocalDateHandler();
        }

        return dateHandler;
    }
}




















