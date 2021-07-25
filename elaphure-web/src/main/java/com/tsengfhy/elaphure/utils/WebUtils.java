package com.tsengfhy.elaphure.utils;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Optional;

@UtilityClass
public final class WebUtils {

    public static Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()))
                .map(ServletRequestAttributes::getRequest);
    }

    public static Optional<HttpServletResponse> getResponse() {
        return Optional.ofNullable(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()))
                .map(ServletRequestAttributes::getResponse);
    }

    @Setter
    private static ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();

    public static boolean isRest() {
        Optional.ofNullable(contentNegotiationManager).orElseThrow(UnsupportedOperationException::new);

        return getRequest()
                .map(request -> {
                    try {
                        return contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
                    } catch (HttpMediaTypeNotAcceptableException e) {
                        return null;
                    }
                })
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(mediaType -> mediaType.getQualityValue() == 1 && MediaType.APPLICATION_JSON.isCompatibleWith(mediaType));
    }

    public static boolean isAjax() {
        return getRequest()
                .map(request -> request.getHeader("X-Requested-With"))
                .map("XMLHttpRequest"::equals)
                .orElse(false);
    }
}
