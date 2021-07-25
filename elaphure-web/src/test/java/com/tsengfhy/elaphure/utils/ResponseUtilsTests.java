package com.tsengfhy.elaphure.utils;

import com.tsengfhy.elaphure.constants.WebMessages;
import com.tsengfhy.elaphure.web.Response;
import com.tsengfhy.entry.Application;
import com.tsengfhy.entry.exception.TestException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = Application.class)
class ResponseUtilsTests {

    @Autowired
    private ServerProperties serverProperties;

    @Test
    void testSuccess() {
        String message = RandomStringUtils.random(10);
        int data = RandomUtils.nextInt();
        Response<Integer> response = ResponseUtils.success(message, data);
        Assertions.assertEquals(response.getStatus(), HttpStatus.OK.value());
        Assertions.assertEquals(response.getMessage(), message);
        Assertions.assertEquals(response.getData(), data);
    }

    @Test
    void testFailure() {
        Response<Object> response = ResponseUtils.failure(new TestException());
        Assertions.assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());

        response = ResponseUtils.failure(440, "Login Time-out", "");
        Assertions.assertEquals(response.getStatus(), 440);
        Assertions.assertEquals(response.getError(), "Login Time-out");
    }

    @Test
    void testDesensitized() {
        Exception exception = new RuntimeException("test");
        boolean isIncludeException = serverProperties.getError().isIncludeException();
        ErrorProperties.IncludeAttribute includeMessage = serverProperties.getError().getIncludeMessage();
        ErrorProperties.IncludeAttribute includeStacktrace = serverProperties.getError().getIncludeStacktrace();

        serverProperties.getError().setIncludeException(false);
        serverProperties.getError().setIncludeMessage(ErrorProperties.IncludeAttribute.NEVER);
        serverProperties.getError().setIncludeStacktrace(ErrorProperties.IncludeAttribute.NEVER);
        Response<Object> response = ResponseUtils.failure(exception);
        Assertions.assertNull(response.getException());
        Assertions.assertEquals(response.getMessage(), WebMessages.NO_MESSAGE);
        Assertions.assertNull(response.getTrace());

        serverProperties.getError().setIncludeException(true);
        serverProperties.getError().setIncludeMessage(ErrorProperties.IncludeAttribute.ALWAYS);
        serverProperties.getError().setIncludeStacktrace(ErrorProperties.IncludeAttribute.ALWAYS);
        response = ResponseUtils.failure(exception);
        Assertions.assertNotNull(response.getException());
        Assertions.assertEquals(response.getMessage(), exception.getMessage());
        Assertions.assertNotNull(response.getTrace());

        serverProperties.getError().setIncludeException(isIncludeException);
        serverProperties.getError().setIncludeMessage(includeMessage);
        serverProperties.getError().setIncludeStacktrace(includeStacktrace);
    }
}
