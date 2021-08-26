package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.env.SqsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({SqsProperties.class})
public class SqsConfig {
}
