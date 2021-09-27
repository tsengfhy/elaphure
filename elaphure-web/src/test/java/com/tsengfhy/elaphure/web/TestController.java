package com.tsengfhy.elaphure.web;

import io.swagger.annotations.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Api(tags = {"测试接口"})
@RestController
@RequestMapping("/test")
public class TestController {

    @ApiOperation("Get接口")
    @GetMapping
    public String get(@ApiParam("字符串") String value) {
        return value;
    }

    @ApiOperation("Post接口")
    @PostMapping
    public TestDTO post(@Valid @RequestBody TestDTO dto) {
        return dto;
    }

    @ApiOperation("异常接口")
    @GetMapping("/error")
    public void error() {
        throw new TestException();
    }

    @ApiModel("测试Form")
    @Data
    @Accessors(chain = true)
    public static class TestDTO {

        @ApiModelProperty(value = "字符串", required = true)
        @NotEmpty(message = "{test.value}")
        private String value;
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "{test.error}")
    public static class TestException extends IllegalArgumentException {
    }
}
