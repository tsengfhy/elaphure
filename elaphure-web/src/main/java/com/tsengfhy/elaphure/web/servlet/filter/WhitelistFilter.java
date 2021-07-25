package com.tsengfhy.elaphure.web.servlet.filter;

import lombok.Getter;
import lombok.Setter;
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

@Getter
@Setter
public abstract class WhitelistFilter extends OncePerRequestFilter {

    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private PathMatcher pathMatcher = new AntPathMatcher();
    private List<String> allowedPaths = Collections.emptyList();

    @Override
    public void initFilterBean() {
        Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        Assert.notNull(allowedPaths, "AllowedPaths must not be null");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = urlPathHelper.getLookupPathForRequest(request);
        if (allowedPaths.stream().anyMatch(allowedPath -> pathMatcher.match(allowedPath, path))) {
            filterChain.doFilter(request, response);
        } else {
            doFilter(request, response, filterChain);
        }
    }

    protected abstract void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;
}
