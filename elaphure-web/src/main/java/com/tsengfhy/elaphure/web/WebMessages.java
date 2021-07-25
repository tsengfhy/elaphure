package com.tsengfhy.elaphure.web;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebMessages {

    NO_MESSAGE("web.noMessage","No message available");

    private final String key;
    private final String message;
}
