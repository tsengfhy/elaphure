package com.tsengfhy.elaphure.utils;

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
        Assertions.assertEquals(ExceptionUtils.getStatus(new RuntimeException()), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(ExceptionUtils.getStatus(new TestException()), HttpStatus.BAD_REQUEST);
    }

    @Test
    void testGetMessage() {
        Optional.of("this is not an i18n error").ifPresent(message -> {
            Assertions.assertEquals(ExceptionUtils.getMessage(new RuntimeException(message)), message);
        });
        Optional.of("test.error").ifPresent(message -> {
            Assertions.assertEquals(ExceptionUtils.getMessage(new TestException()), MessageUtils.getMessage(message));
        });
    }

    @Test
    void testFindResponseStatus() {
        Assertions.assertNull(ExceptionUtils.findResponseStatus(new RuntimeException()));
        Assertions.assertNotNull(ExceptionUtils.findResponseStatus(new RuntimeException(new TestException())));
    }
}
