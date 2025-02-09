package com.tsengfhy.elaphure.exception;

import com.tsengfhy.elaphure.constant.WebMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = WebMessages.XSS_REJECT)
public class SensitiveCharacterFoundException extends IllegalArgumentException {
}
