package com.tsengfhy.elaphure.web;

import com.tsengfhy.elaphure.web.servlet.ParameterMappingHttpServletRequest;
import com.tsengfhy.elaphure.web.servlet.WhitelistFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;

public class SupportTests {

    @Test
    void testWhitelistFilter() throws ServletException, IOException {
        final String path = "/test";
        final String name = "test";
        final String value = RandomStringUtils.random(6);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        MockFilterChain mockFilterChain = new MockFilterChain();
        mockRequest.setRequestURI(path);

        Filter filter = WhitelistFilter.builder()
                .filter((request, response, chain) -> ((MockHttpServletRequest) request).setParameter(name, value))
                .build();
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        filter = WhitelistFilter.builder()
                .allowedPaths(Collections.singletonList(path))
                .filter(((request, response, chain) -> Assertions.assertNotEquals(request.getParameter(name), value)))
                .build();
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);
    }

    @Test
    void testParameterMappingHttpServletRequest() {
        final String name = "test";
        final String fixedValue = "test";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(name, RandomStringUtils.random(8));
        Assertions.assertNotEquals(request.getParameter(name), fixedValue);
        Assertions.assertNotEquals(request.getParameterValues(name)[0], fixedValue);
        Assertions.assertNotEquals(request.getParameterMap().get(name)[0], fixedValue);

        ParameterMappingHttpServletRequest requestWrapper = new ParameterMappingHttpServletRequest(request, value -> fixedValue);
        Assertions.assertEquals(requestWrapper.getParameter(name), fixedValue);
        Assertions.assertEquals(requestWrapper.getParameterValues(name)[0], fixedValue);
        Assertions.assertEquals(requestWrapper.getParameterMap().get(name)[0], fixedValue);
    }
}
