package com.tsengfhy.entry.controller;

import com.tsengfhy.entry.message.TestQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TestController.PATH)
public class TestController {

    public static final String PATH = "/test";

    @Autowired
    private StreamBridge streamBridge;

    @PostMapping
    public void post(@RequestBody String value) {
        streamBridge.send(TestQueue.OUT, value);
    }
}
