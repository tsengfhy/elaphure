package com.tsengfhy.elaphure.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParameterChar {

    SEMICOLON(";"),
    EQUAL("="),
    COMMA(",");

    private final String value;
}
