package com.tsengfhy.elaphure.web.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

public interface ErrorAttributeOptionsConfigurer {
    ErrorAttributeOptions getErrorAttributeOptions(HttpServletRequest request, MediaType mediaType);
}
