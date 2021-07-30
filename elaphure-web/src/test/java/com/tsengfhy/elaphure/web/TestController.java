package com.tsengfhy.elaphure.web;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String get(String value) {
        return value;
    }

    @PostMapping
    public TestDTO post(@Valid @RequestBody TestDTO dto) {
        return dto;
    }

    @GetMapping("/error")
    public void error() {
        throw new TestException();
    }

    @Data
    @Accessors(chain = true)
    public static class TestDTO {
        @NotEmpty(message = "{test.value}")
        private String value;
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "{test.error}")
    public static class TestException extends IllegalArgumentException {
    }
}
