package com.tsengfhy.elaphure.sms.exception;

import com.tsengfhy.elaphure.sms.SmsMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "{sms.serverError}")
public class SmsServerException extends SmsException {

    private static final String DEFAULT_MESSAGE = SmsMessages.SERVER_ERROR.getMessage();

    public SmsServerException() {
        super(DEFAULT_MESSAGE);
    }

    public SmsServerException(String message) {
        super(message);
    }

    public SmsServerException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public SmsServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
