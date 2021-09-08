package com.tsengfhy.elaphure.controller;

import com.tsengfhy.elaphure.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RefreshScope
public class TestController {

    @Autowired
    private Environment environment;

    @Autowired
    private TestService testService;

    @GetMapping("/get")
    public String get() {
        String value = environment.getProperty("value");
        String port = environment.getProperty("local.server.port");
        String result = String.format("value: %s, at port %s", value, port);
        log.info(result);
        return result;
    }

    @GetMapping("/remote-get")
    public String remoteGet() {
        return testService.get();
    }
}
