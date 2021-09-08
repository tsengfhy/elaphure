package com.tsengfhy.elaphure.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("elaphure-cloud")
public interface TestService {

    @GetMapping("/get")
    String get();
}
