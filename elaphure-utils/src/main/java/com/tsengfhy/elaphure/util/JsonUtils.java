package com.tsengfhy.elaphure.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;

@UtilityClass
public final class JsonUtils {

    @Setter
    private static ObjectMapper mapper;

    @SneakyThrows(JsonProcessingException.class)
    public static <T> String toJson(@Nullable T t) {
        if (Objects.isNull(t)) {
            return null;
        }

        return getMapper().writeValueAsString(t);
    }

    @SneakyThrows(JsonProcessingException.class)
    public static <T> T fromJson(@Nullable String json, Class<T> classOfT) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        return getMapper().readValue(json, classOfT);
    }

    private static ObjectMapper getMapper() {
        return Optional.ofNullable(mapper).orElseThrow(UnsupportedOperationException::new);
    }
}
