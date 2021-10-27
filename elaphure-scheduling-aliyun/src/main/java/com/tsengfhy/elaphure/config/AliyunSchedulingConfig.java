package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.scheduling.JobDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AliyunSchedulingConfig {

    @Bean
    public JobDelegate jobDelegate() {
        return new JobDelegate();
    }
}
