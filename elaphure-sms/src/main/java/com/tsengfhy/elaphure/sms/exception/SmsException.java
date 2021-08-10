package com.tsengfhy.elaphure.sms.exception;

public class SmsException extends RuntimeException {

    public SmsException() {
        super();
    }

    public SmsException(String message) {
        super(message);
    }

    public SmsException(Throwable cause) {
        super(cause);
    }

    public SmsException(String message, Throwable cause) {
        super(message, cause);
    }
}
