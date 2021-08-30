package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.env.SmsProperties;
import com.tsengfhy.elaphure.sms.annotation.SmsListenerAnnotationBeanPostProcessor;
import com.tsengfhy.elaphure.sms.config.SmsListenerEndpointRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SmsProperties.class})
public class SmsConfig {

    @Bean
    public SmsListenerAnnotationBeanPostProcessor smsListenerAnnotationBeanPostProcessor() {
        return SmsListenerAnnotationBeanPostProcessor.builder()
                .endpointRegistry(smsListenerEndpointRegistry())
                .build();
    }

    @Bean
    public SmsListenerEndpointRegistry smsListenerEndpointRegistry() {
        return new SmsListenerEndpointRegistry();
    }
}
