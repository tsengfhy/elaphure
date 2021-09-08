package com.tsengfhy.entry.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "elaphure-cloud", fallback = TestServiceFallback.class)
public interface TestService {

    @GetMapping("/local")
    String local(@RequestParam String value);
}
