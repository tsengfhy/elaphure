package com.tsengfhy.elaphure.web.error;

import com.tsengfhy.elaphure.utils.ExceptionExtUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Optional;

public class AdvancedErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Throwable getError(WebRequest webRequest) {
        Throwable exception = super.getError(webRequest);
        ExceptionUtils.printRootCauseStackTrace(exception);
        return  exception;
    }

    @Override
    protected String getMessage(WebRequest webRequest, Throwable error) {
        return Optional.ofNullable(error)
                .map(ExceptionExtUtils::getMessage)
                .orElseGet(() -> super.getMessage(webRequest, error));
    }
}
