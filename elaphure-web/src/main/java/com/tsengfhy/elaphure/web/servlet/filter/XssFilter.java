package com.tsengfhy.elaphure.web.servlet.filter;

import com.tsengfhy.elaphure.util.XssUtils;
import com.tsengfhy.elaphure.web.servlet.http.ParameterMappingRequestWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class XssFilter extends WhitelistFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(new ParameterMappingRequestWrapper(request, XssUtils::process), response);
    }
}
