package com.tsengfhy.elaphure.web;

import com.tsengfhy.elaphure.web.servlet.filter.WhitelistFilter;
import com.tsengfhy.elaphure.web.servlet.http.HttpHeaderRequestWrapper;
import com.tsengfhy.elaphure.web.servlet.http.ParameterMappingRequestWrapper;
import com.tsengfhy.entry.controller.TestController;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

class WebSupportTests {

    private static final String NAME = "test";
    private static final String VALUE = "test";

    @Test
    void testWhitelistFilter() throws ServletException, IOException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        MockFilterChain mockFilterChain = new MockFilterChain();
        mockRequest.setRequestURI(TestController.PATH);

        WhitelistFilter filter = new WhitelistFilter() {
            @Override
            protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                ((MockHttpServletRequest) request).setParameter(NAME, VALUE);
            }
        };
        filter.setAllowedPaths(Collections.singletonList(TestController.PATH));
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        filter = new WhitelistFilter() {
            @Override
            protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                Assertions.assertNotEquals(VALUE, request.getParameter(NAME));
            }
        };
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);
    }

    @Test
    void testParameterMappingRequestWrapper() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(NAME, RandomStringUtils.random(8));
        Assertions.assertNotEquals(VALUE, request.getParameter(NAME));
        Assertions.assertNotEquals(VALUE, request.getParameterValues(NAME)[0]);
        Assertions.assertNotEquals(VALUE, request.getParameterMap().get(NAME)[0]);

        ParameterMappingRequestWrapper requestWrapper = new ParameterMappingRequestWrapper(request, value -> VALUE);
        Assertions.assertEquals(VALUE, requestWrapper.getParameter(NAME));
        Assertions.assertEquals(VALUE, requestWrapper.getParameterValues(NAME)[0]);
        Assertions.assertEquals(VALUE, requestWrapper.getParameterMap().get(NAME)[0]);
    }

    @Test
    void testHttpHeaderRequestWrapper() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Assertions.assertFalse(request.getHeaderNames().hasMoreElements());
        Assertions.assertFalse(request.getHeaders(NAME).hasMoreElements());
        Assertions.assertNull(request.getHeader(NAME));

        HttpHeaderRequestWrapper requestWrapper = new HttpHeaderRequestWrapper(request);
        requestWrapper.setHeader(NAME, RandomStringUtils.random(8));
        Assertions.assertTrue(requestWrapper.getHeaderNames().hasMoreElements());
        Assertions.assertTrue(requestWrapper.getHeaders(NAME).hasMoreElements());
        Assertions.assertNotNull(requestWrapper.getHeader(NAME));
    }
}
