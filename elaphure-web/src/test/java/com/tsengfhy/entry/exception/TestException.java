package com.tsengfhy.entry.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "test.error")
public class TestException extends IllegalArgumentException {
}
