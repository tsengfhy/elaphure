package com.tsengfhy.elaphure.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

@UtilityClass
public final class HttpUtils {

    public static HttpServletRequest getRequest() {
        return Optional.ofNullable(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()))
                .map(ServletRequestAttributes::getRequest)
                .orElseThrow(UnsupportedOperationException::new);
    }

    public static HttpServletResponse getResponse() {
        return Optional.ofNullable(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()))
                .map(ServletRequestAttributes::getResponse)
                .orElseThrow(UnsupportedOperationException::new);
    }

    public static boolean isError(int status) {
        return status >= HttpStatus.BAD_REQUEST.value();
    }
}
