package com.tsengfhy.elaphure.sms.exception;

import com.tsengfhy.elaphure.sms.SmsMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "{sms.clientError}")
public class SmsClientException extends SmsException {

    private static final String DEFAULT_MESSAGE = SmsMessages.CLIENT_ERROR.getMessage();

    public SmsClientException() {
        super(DEFAULT_MESSAGE);
    }

    public SmsClientException(String message) {
        super(message);
    }

    public SmsClientException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public SmsClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
