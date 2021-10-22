package com.tsengfhy.elaphure.exception;

import org.springframework.scheduling.SchedulingException;

public class NoSuchJobException extends SchedulingException {

    public NoSuchJobException(String jobName) {
        super("No job named '" + jobName + "' available");
    }

    public NoSuchJobException(String jobName, Throwable cause) {
        super("No job named '" + jobName + "' available", cause);
    }
}
