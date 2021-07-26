package com.tsengfhy.elaphure.utils;

import com.tsengfhy.elaphure.web.TestController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ExceptionExtUtilsTests {

    @Test
    void testGetMessage() {
        final String notI18n = "this is not a i18n message";
        Assertions.assertEquals(ExceptionExtUtils.getMessage(new RuntimeException(notI18n)), notI18n);
        final String errorCode = "test.error";
        Assertions.assertEquals(ExceptionExtUtils.getMessage(new TestController.TestException()), MessageUtils.getMessage(errorCode));
    }

    @Test
    void testResolveResponseStatus() {
        Assertions.assertNotNull(ExceptionExtUtils.resolveResponseStatus(new RuntimeException(new TestController.TestException())));
        Assertions.assertNull(ExceptionExtUtils.resolveResponseStatus(new RuntimeException()));
        Assertions.assertNull(ExceptionExtUtils.resolveResponseStatus(null));
    }
}
