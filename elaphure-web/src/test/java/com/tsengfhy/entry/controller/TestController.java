package com.tsengfhy.entry.controller;

import com.tsengfhy.entry.domain.Domain;
import com.tsengfhy.entry.exception.TestException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(TestController.PATH)
public class TestController {

    public static final String PATH = "/test";

    @GetMapping
    public String get(String value) {
        return value;
    }

    @PostMapping
    public Domain post(@Valid @RequestBody Domain domain) {
        return domain;
    }

    @GetMapping(":error")
    public void error() {
        throw new TestException();
    }
}
