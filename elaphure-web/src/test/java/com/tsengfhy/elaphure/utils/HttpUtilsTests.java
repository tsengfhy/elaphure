package com.tsengfhy.elaphure.utils;

import com.tsengfhy.entry.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class HttpUtilsTests {

    @Test
    void testGetRequest() {
        Assertions.assertThrows(UnsupportedOperationException.class, HttpUtils::getRequest);
    }

    @Test
    void testGetResponse() {
        Assertions.assertThrows(UnsupportedOperationException.class, HttpUtils::getResponse);
    }

    @Test
    void testIsError() {
        Assertions.assertFalse(HttpUtils.isError(HttpStatus.OK.value()));
        Assertions.assertFalse(HttpUtils.isError(HttpStatus.FOUND.value()));
        Assertions.assertTrue(HttpUtils.isError(HttpStatus.BAD_REQUEST.value()));
        Assertions.assertTrue(HttpUtils.isError(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
