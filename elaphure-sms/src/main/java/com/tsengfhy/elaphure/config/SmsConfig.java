package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.env.SmsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({SmsProperties.class})
public class SmsConfig {
}
