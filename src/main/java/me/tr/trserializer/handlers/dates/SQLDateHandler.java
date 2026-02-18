package me.tr.trserializer.handlers.dates;

import me.tr.trserializer.handlers.TypeHandler;
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
        if (optHandler.isEmpty()) return new LocalDateHandler();

        TypeHandler handler = optHandler.get();
        if (!(handler instanceof LocalDateHandler dateHandler)) return new LocalDateHandler();


        return dateHandler;
    }
}




















