package com.tsengfhy.elaphure.utils;

import com.tsengfhy.elaphure.web.Response;
import com.tsengfhy.elaphure.web.error.ErrorAttributeOptionsConfigurer;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Optional;

@UtilityClass
public final class ResponseUtils {

    @Setter
    private static ErrorAttributeOptionsConfigurer optionsConfigurer;

    public static <T> Response<T> success(@Nullable String message, @Nullable T data) {
        return action(HttpStatus.OK.value(), null, null, message, null, data);
    }

    public static Response<Object> failure(HttpStatus httpStatus, Exception e) {
        return failure(httpStatus.value(), httpStatus.getReasonPhrase(), e);
    }

    public static Response<Object> failure(int status, String error, Exception e) {
        return action(
                status,
                error,
                e.getClass().getName(),
                e.getMessage(),
                ExceptionUtils.getStackTrace(e),
                null
        );
    }

    public static Response<Object> failure(HttpStatus httpStatus, String message) {
        return failure(httpStatus.value(), httpStatus.getReasonPhrase(), message);
    }

    public static Response<Object> failure(int status, String error, String message) {
        return action(status, error, null, message, null, null);
    }

    public static <T> Response<T> action(int status, String error, String exception, String message, String trace, T data) {
        ErrorAttributeOptions options = getOptions();
        Response<T> response = new Response<>();
        response.setTimestamp(OffsetDateTime.now());
        response.setStatus(status);
        response.setError(error);
        if (options.isIncluded(ErrorAttributeOptions.Include.EXCEPTION)) {
            response.setException(exception);
        }
        if (options.isIncluded(ErrorAttributeOptions.Include.MESSAGE)) {
            response.setMessage(message);
        } else {
            response.setMessage("");
        }
        if (options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE)) {
            response.setTrace(trace);
        }
        response.setPath(getRequest().getRequestURI());
        response.setData(data);
        return response;
    }

    private static ErrorAttributeOptions getOptions() {
        return Optional.ofNullable(optionsConfigurer)
                .map(configurer -> configurer.getErrorAttributeOptions(getRequest(), MediaType.ALL))
                .orElseThrow(UnsupportedOperationException::new);
    }

    private static HttpServletRequest getRequest() {
        return WebUtils.getRequest().orElseThrow(UnsupportedOperationException::new);
    }
}
