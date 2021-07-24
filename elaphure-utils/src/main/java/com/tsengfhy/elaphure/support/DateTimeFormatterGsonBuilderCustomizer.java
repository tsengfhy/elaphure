package com.tsengfhy.elaphure.support;

import com.google.gson.*;
import com.tsengfhy.elaphure.constants.DateFormat;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.core.Ordered;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.function.BiFunction;

public class DateTimeFormatterGsonBuilderCustomizer implements GsonBuilderCustomizer, Ordered {

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public void customize(GsonBuilder gsonBuilder) {
        gsonBuilder
                .setDateFormat(DateFormat.ISO_DATETIME_FORMAT)
                .registerTypeAdapter(LocalDate.class, new DateTimeAdapter<>(DateTimeFormatter.ISO_DATE, LocalDate::parse))
                .registerTypeAdapter(LocalTime.class, new DateTimeAdapter<>(DateTimeFormatter.ISO_TIME, LocalTime::parse))
                .registerTypeAdapter(LocalDateTime.class, new DateTimeAdapter<>(DateTimeFormatter.ISO_DATE_TIME, LocalDateTime::parse))
                .registerTypeAdapter(OffsetDateTime.class, new DateTimeAdapter<>(DateTimeFormatter.ISO_DATE_TIME, OffsetDateTime::parse))
        ;
    }

    private static class DateTimeAdapter<T extends TemporalAccessor> implements JsonSerializer<T>, JsonDeserializer<T> {

        private final DateTimeFormatter formatter;

        private final BiFunction<String, DateTimeFormatter, T> deserialize;

        DateTimeAdapter(DateTimeFormatter formatter, BiFunction<String, DateTimeFormatter, T> deserialize) {
            this.formatter = formatter;
            this.deserialize = deserialize;
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return deserialize.apply(json.getAsJsonPrimitive().getAsString(), formatter);
        }
    }
}
