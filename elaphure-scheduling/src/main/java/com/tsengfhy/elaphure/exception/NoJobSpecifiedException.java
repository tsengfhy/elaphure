package com.tsengfhy.elaphure.exception;

import org.springframework.scheduling.SchedulingException;

public class NoJobSpecifiedException extends SchedulingException {

    public NoJobSpecifiedException() {
        super("Expecting at least one jobName to proceed, but none was passed");
    }

    public NoJobSpecifiedException(Throwable cause) {
        super("Expecting at least one jobName to proceed, but none was passed", cause);
    }
}
