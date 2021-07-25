package com.tsengfhy.elaphure.web.servlet.filter;

import com.tsengfhy.elaphure.utils.XssUtils;
import com.tsengfhy.elaphure.web.servlet.http.ParameterMappingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class XssFilter extends WhitelistFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(new ParameterMappingRequestWrapper(request, XssUtils::process), response);
    }
}
