package com.tsengfhy.elaphure.sms.exception;

public class ListenerExecutionFailedException extends SmsException {

    public ListenerExecutionFailedException() {
        super();
    }

    public ListenerExecutionFailedException(String message) {
        super(message);
    }

    public ListenerExecutionFailedException(Throwable cause) {
        super(cause);
    }

    public ListenerExecutionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
