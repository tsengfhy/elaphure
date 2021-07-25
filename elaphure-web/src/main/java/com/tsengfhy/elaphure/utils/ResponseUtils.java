package com.tsengfhy.elaphure.utils;

import com.tsengfhy.elaphure.web.Response;
import com.tsengfhy.elaphure.constants.WebMessages;
import com.tsengfhy.elaphure.web.error.ErrorAttributeOptionsConfigurer;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import java.time.OffsetDateTime;
import java.util.Optional;

@UtilityClass
public final class ResponseUtils {

    @Setter
    private static ErrorAttributeOptionsConfigurer errorAttributeOptionsConfigurer;

    public static <T> Response<T> success(@Nullable String message) {
        return success(message, null);
    }

    public static <T> Response<T> success(@Nullable String message, @Nullable T data) {
        return action(HttpStatus.OK.value(), null, null, null, message, data);
    }

    public static Response<Object> failure(Exception e) {
        return failure(ExceptionUtils.getStatus(e), e);
    }

    public static Response<Object> failure(HttpStatus status, Exception e) {
        return failure(status.value(), status.getReasonPhrase(), e);
    }

    public static Response<Object> failure(int status, String error, Exception e) {
        return action(status, error, e.getClass().getName(), org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e), ExceptionUtils.getMessage(e), null);
    }

    public static Response<Object> failure(HttpStatus status, String message) {
        return failure(status.value(), status.getReasonPhrase(), message);
    }

    public static Response<Object> failure(int status, String error, String message) {
        return action(status, error, null, null, message, null);
    }

    private static <T> Response<T> action(int status, String error, String exception, String trace, String message, T data) {
        boolean isError = HttpUtils.isError(status);
        ErrorAttributeOptions options = getOptions();

        Response<T> response = new Response<>();
        response.setTimestamp(OffsetDateTime.now());
        response.setStatus(status);
        if (isError) {
            if (StringUtils.isNotBlank(error)) {
                response.setError(error);
            } else {
                try {
                    response.setError(HttpStatus.valueOf(status).getReasonPhrase());
                } catch (IllegalArgumentException e) {
                    response.setError("Http Status " + status);
                }
            }
            if (options.isIncluded(ErrorAttributeOptions.Include.EXCEPTION)) {
                response.setException(exception);
            }
            if (options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE)) {
                response.setTrace(trace);
            }
        }
        if ((!isError || options.isIncluded(ErrorAttributeOptions.Include.MESSAGE)) && StringUtils.isNotBlank(message)) {
            response.setMessage(message);
        } else {
            response.setMessage(MessageUtils.getMessage(WebMessages.NO_MESSAGE, WebMessages.NO_MESSAGE));
        }
        response.setPath(HttpUtils.getRequest().getRequestURI());
        response.setData(data);

        return response;
    }

    private static ErrorAttributeOptions getOptions() {
        return Optional.ofNullable(errorAttributeOptionsConfigurer)
                .map(configurer -> configurer.getErrorAttributeOptions(HttpUtils.getRequest(), MediaType.APPLICATION_JSON))
                .orElseThrow(UnsupportedOperationException::new);
    }
}
