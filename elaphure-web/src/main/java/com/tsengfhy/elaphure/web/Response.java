package com.tsengfhy.elaphure.web;

import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1731283405791055795L;

    private OffsetDateTime timestamp;
    private Integer status;
    private String error;
    private String exception;
    private String trace;
    private String message;
    private String path;
    private T data;
}
