package com.tsengfhy.entry.service;

import org.springframework.stereotype.Component;

@Component
public class TestServiceFallback implements TestService {

    @Override
    public String local(String value) {
        return "Fall Back";
    }
}
