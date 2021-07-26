package com.tsengfhy.elaphure.web;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/test")
public class TestController {

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
