package com.tsengfhy.elaphure.util;

import com.tsengfhy.entry.Application;
import com.tsengfhy.entry.exception.TestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@SpringBootTest(classes = Application.class)
class ExceptionUtilsTests {

    @Test
    void testGetStatus() {
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStatus(new RuntimeException()));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ExceptionUtils.getStatus(new TestException()));
    }

    @Test
    void testGetMessage() {
        Optional.of("this is not an i18n error").ifPresent(message -> {
            Assertions.assertEquals(message, ExceptionUtils.getMessage(new RuntimeException(message)));
        });
        Optional.of("test.error").ifPresent(message -> {
            Assertions.assertEquals(MessageUtils.getMessage(message), ExceptionUtils.getMessage(new TestException()));
        });
    }

    @Test
    void testFindResponseStatus() {
        Assertions.assertNull(ExceptionUtils.findResponseStatus(new RuntimeException()));
        Assertions.assertNotNull(ExceptionUtils.findResponseStatus(new RuntimeException(new TestException())));
    }
}
