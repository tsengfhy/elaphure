package com.tsengfhy.elaphure.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@SpringBootTest
public class WebUtilsTests {

    @Test
    void testRest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        request.addHeader(HttpHeaders.ACCEPT, MediaType.TEXT_HTML);
        Assertions.assertFalse(WebUtils.isRest());

        request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        Assertions.assertTrue(WebUtils.isRest());
    }

    @Test
    void testAjax() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        Assertions.assertFalse(WebUtils.isAjax());

        request.addHeader("X-Requested-With", "XMLHttpRequest");
        Assertions.assertTrue(WebUtils.isAjax());
    }
}
