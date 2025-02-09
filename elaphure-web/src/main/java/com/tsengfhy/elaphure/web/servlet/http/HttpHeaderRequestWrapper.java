package com.tsengfhy.elaphure.web.servlet.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class HttpHeaderRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, List<String>> headers = new HashMap<>();

    public HttpHeaderRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void setHeader(String name, String value) {
        this.headers.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> set = new HashSet<>(this.headers.keySet());
        set.addAll(Collections.list(super.getHeaderNames()));
        return Collections.enumeration(set);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> headers = new ArrayList<>(this.headers.get(name));
        headers.addAll(Collections.list(super.getHeaders(name)));
        return Collections.enumeration(headers);
    }

    @Override
    public String getHeader(String name) {
        return getHeaders(name).nextElement();
    }
}
