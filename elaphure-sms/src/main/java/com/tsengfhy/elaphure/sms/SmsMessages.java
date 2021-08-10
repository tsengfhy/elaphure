package com.tsengfhy.elaphure.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmsMessages {

    CLIENT_ERROR("sms.clientError", "Send SMS with incorrect parameters"),
    SERVER_ERROR("sms.serverError", "SMS vendor encounter error");

    private final String key;
    private final String message;
}
