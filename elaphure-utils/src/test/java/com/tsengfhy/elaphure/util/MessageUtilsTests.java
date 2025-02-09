package com.tsengfhy.elaphure.util;

import com.tsengfhy.entry.Application;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@SpringBootTest(classes = Application.class)
class MessageUtilsTests {

    private static final String KEY = "test";

    private static final String DEFAULT_MESSAGE = "default";

    @Test
    void testGetMessage() {
        Assertions.assertNotNull(MessageUtils.getMessage(KEY));
        Assertions.assertThrows(NoSuchMessageException.class, () -> {
            MessageUtils.getMessage(RandomStringUtils.random(6));
        });
    }

    @Test
    void testGetDefaultMessage() {
        Assertions.assertNotEquals(DEFAULT_MESSAGE, MessageUtils.getMessage(KEY, DEFAULT_MESSAGE));
        Assertions.assertEquals(DEFAULT_MESSAGE, MessageUtils.getMessage(RandomStringUtils.random(6), DEFAULT_MESSAGE));
    }

    @Test
    void testI18N() {
        Locale locale = LocaleContextHolder.getLocale();
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Assertions.assertTrue(MessageUtils.getMessage(KEY).matches("[a-zA-Z0-9]+"));
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        Assertions.assertTrue(MessageUtils.getMessage(KEY).matches("[\\u4e00-\\u9fa5]+"));
        LocaleContextHolder.setLocale(locale);
    }
}
