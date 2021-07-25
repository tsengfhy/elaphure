package com.tsengfhy.elaphure.web.error;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

public class ErrorAttributeOptionsConfigurerAdapter extends BasicErrorController implements ErrorAttributeOptionsConfigurer {

    public ErrorAttributeOptionsConfigurerAdapter(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
    }

    @Override
    public ErrorAttributeOptions getErrorAttributeOptions(HttpServletRequest request, MediaType mediaType) {
        return super.getErrorAttributeOptions(request, mediaType);
    }
}
