package com.tsengfhy.elaphure.web.xss;

import com.tsengfhy.elaphure.web.WebMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "{web.xssReject}")
public class SensitiveCharacterFoundException extends IllegalArgumentException {

    public SensitiveCharacterFoundException() {
        super(WebMessages.XSS_REJECT.getMessage());
    }
}
