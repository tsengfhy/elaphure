package com.tsengfhy.elaphure.utils;

import com.tsengfhy.elaphure.web.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
public class ResponseUtilsTests {

    @Test
    void testSuccess() {
        String message = RandomStringUtils.random(10);
        int data = RandomUtils.nextInt();
        Response<Integer> response = ResponseUtils.success(message, data);
        Assertions.assertEquals(response.getStatus(), HttpStatus.OK.value());
        Assertions.assertEquals(response.getData(), data);
    }

    @Test
    void testFailure() {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = RandomStringUtils.random(10);
        Response<Object> response = ResponseUtils.failure(httpStatus, message);
        Assertions.assertEquals(response.getStatus(), httpStatus.value());
        Assertions.assertEquals(response.getError(), httpStatus.getReasonPhrase());
    }

    @Autowired
    private ServerProperties serverProperties;

    @Test
    void testDesensitized() {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        Exception exception = new RuntimeException("test");
        ErrorProperties.IncludeAttribute includeMessage = serverProperties.getError().getIncludeMessage();
        ErrorProperties.IncludeStacktrace includeStacktrace = serverProperties.getError().getIncludeStacktrace();

        serverProperties.getError().setIncludeMessage(ErrorProperties.IncludeAttribute.NEVER);
        serverProperties.getError().setIncludeStacktrace(ErrorProperties.IncludeStacktrace.NEVER);
        Response<Object> response = ResponseUtils.failure(httpStatus, exception);
        Assertions.assertNotEquals(response.getMessage(), exception.getMessage());
        Assertions.assertNull(response.getTrace());

        serverProperties.getError().setIncludeMessage(ErrorProperties.IncludeAttribute.ALWAYS);
        serverProperties.getError().setIncludeStacktrace(ErrorProperties.IncludeStacktrace.ALWAYS);
        response = ResponseUtils.failure(httpStatus, exception);
        Assertions.assertEquals(response.getMessage(), exception.getMessage());
        Assertions.assertNotNull(response.getTrace());

        serverProperties.getError().setIncludeMessage(includeMessage);
        serverProperties.getError().setIncludeStacktrace(includeStacktrace);
    }
}
