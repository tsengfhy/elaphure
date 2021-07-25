package com.tsengfhy.elaphure.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;

@UtilityClass
public final class ExceptionUtils {

    public static HttpStatus getStatus(Throwable error) {
        return Optional.ofNullable(findResponseStatus(error))
                .map(ResponseStatus::code)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static String getMessage(Throwable error) {
        return Optional.ofNullable(findResponseStatus(error))
                .map(ResponseStatus::reason)
                .map(reason -> MessageUtils.getMessage(reason, reason))
                .filter(StringUtils::isNotBlank)
                .orElse(error.getMessage());
    }

    @Nullable
    public static ResponseStatus findResponseStatus(Throwable error) {
        if (Objects.isNull(error)) {
            return null;
        }

        if (error instanceof ResponseStatusException) {
            return new ResponseStatus() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return ResponseStatus.class;
                }

                @NonNull
                @Override
                public HttpStatus value() {
                    return ((ResponseStatusException) error).getStatus();
                }

                @NonNull
                @Override
                public HttpStatus code() {
                    return ((ResponseStatusException) error).getStatus();
                }

                @NonNull
                @Override
                public String reason() {
                    return Optional.ofNullable(((ResponseStatusException) error).getReason()).orElse("");
                }
            };
        }

        return Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(error.getClass(), ResponseStatus.class))
                .orElseGet(() -> findResponseStatus(error.getCause()));
    }
}
