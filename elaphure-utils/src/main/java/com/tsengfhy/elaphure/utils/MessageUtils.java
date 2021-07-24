package com.tsengfhy.elaphure.utils;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

import java.util.Optional;

@Slf4j
@UtilityClass
public final class MessageUtils {

    @Setter
    private static MessageSource messageSource;

    public static String getMessage(String code) throws NoSuchMessageException {
        return getMessage(code, new Object[]{});
    }

    public static String getMessage(String code, Object[] args) throws NoSuchMessageException {
        return doGetMessage(code, args, null).orElseThrow(() -> new NoSuchMessageException(code, LocaleContextHolder.getLocale()));
    }

    public static String getMessage(String code, String defaultMessage) {
        return getMessage(code, new Object[]{}, defaultMessage);
    }

    public static String getMessage(String code, Object[] args, String defaultMessage) {
        return doGetMessage(code, args, defaultMessage).orElse(defaultMessage);
    }

    private static Optional<String> doGetMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage) {
        return Optional.ofNullable(getMessageSource().getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale()));
    }

    private static MessageSource getMessageSource() {
        return Optional.ofNullable(messageSource).orElseThrow(UnsupportedOperationException::new);
    }
}
