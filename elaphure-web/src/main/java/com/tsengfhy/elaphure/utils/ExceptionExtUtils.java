package com.tsengfhy.elaphure.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public final class ExceptionExtUtils {

    private static final Pattern CODE_PATTERN = Pattern.compile("^\\{([^}]*)\\}$");

    public static String getMessage(Throwable error) {
        return Optional.ofNullable(resolveResponseStatus(error))
                .map(ResponseStatus::reason)
                .map(reason -> {
                    Matcher matcher = CODE_PATTERN.matcher(reason);
                    return matcher.find() ? MessageUtils.getMessage(matcher.group(1), "") : reason;
                })
                .filter(StringUtils::isNotBlank)
                .orElse(error.getMessage());
    }

    public static ResponseStatus resolveResponseStatus(Throwable error) {
        if (error == null) {
            return null;
        }

        return Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(error.getClass(), ResponseStatus.class))
                .orElseGet(() -> resolveResponseStatus(error.getCause()));
    }
}
