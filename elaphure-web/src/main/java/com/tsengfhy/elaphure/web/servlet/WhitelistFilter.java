package com.tsengfhy.elaphure.web.servlet;

import lombok.Builder;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Builder
public class WhitelistFilter extends OncePerRequestFilter implements OrderedFilter {

    @Builder.Default
    private final int order = REQUEST_WRAPPER_FILTER_MAX_ORDER;
    @Builder.Default
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();
    @Builder.Default
    private final PathMatcher pathMatcher = new AntPathMatcher();
    @Builder.Default
    private final List<String> allowedPaths = Collections.emptyList();
    private final FilterConsumer filter;

    @Override
    public void initFilterBean() {
        Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        Assert.notNull(allowedPaths, "AllowedPaths must not be null");
        Assert.notNull(filter, "FilterFunction must not be null");
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = urlPathHelper.getLookupPathForRequest(request);
        if (allowedPaths.stream().anyMatch(allowedPath -> pathMatcher.match(allowedPath, path))) {
            filterChain.doFilter(request, response);
        } else {
            filter.doFilter(request, response, filterChain);
        }
    }

    @FunctionalInterface
    public interface FilterConsumer {
        void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException;
    }
}
