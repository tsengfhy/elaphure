package com.tsengfhy.entry.controller;

import com.alibaba.nacos.common.utils.StringUtils;
import com.tsengfhy.entry.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private Environment environment;

    @Autowired
    private TestService testService;

    @GetMapping("/local")
    public String local(String value) {
        if (StringUtils.isNotBlank(value)) {
            if ("error".equals(value)) {
                throw new IllegalArgumentException();
            }
            return value;
        }
        return environment.getProperty("value");
    }

    @GetMapping("/remote")
    public String remote(String value) {
        return testService.local(value);
    }
}
