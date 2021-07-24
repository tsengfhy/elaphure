package com.tsengfhy.elaphure.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.Optional;

@UtilityClass
public final class JsonUtils {

    @Setter
    private static Gson gson = new GsonBuilder().create();

    public Gson getGson() {
        return Optional.ofNullable(gson).orElseThrow(UnsupportedOperationException::new);
    }

    public static <T> String toJson(@Nullable T t) {
        return Optional.ofNullable(t)
                .map(getGson()::toJson)
                .orElse(null);
    }

    public static <T> T fromJson(@Nullable String json, Class<T> classOfT) {
        return Optional.ofNullable(json)
                .filter(StringUtils::isNotBlank)
                .map(value -> getGson().fromJson(value, classOfT))
                .orElse(null);
    }
}
