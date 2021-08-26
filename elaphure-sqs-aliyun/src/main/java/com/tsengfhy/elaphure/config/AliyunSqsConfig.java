package com.tsengfhy.elaphure.config;

import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.extended.javamessaging.MNSConnectionFactory;
import com.tsengfhy.elaphure.env.ContextProperties;
import com.tsengfhy.elaphure.env.SqsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.ConnectionFactory;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({MNSConnectionFactory.class, MNSClient.class})
public class AliyunSqsConfig {

    @Autowired
    private SqsProperties properties;

    @Autowired
    private ContextProperties contextProperties;

    @Bean
    @ConditionalOnMissingBean
    public ConnectionFactory connectionFactory() {
        return MNSConnectionFactory.builder()
                .withEndpoint(properties.getEndpoint())
                .withAccessKeyId(Optional.ofNullable(properties.getAccessKey()).orElseGet(contextProperties::getAccessKey))
                .withAccessKeySecret(Optional.ofNullable(properties.getSecretKey()).orElseGet(contextProperties::getSecretKey))
                .build();
    }

    @Autowired
    void configureJmsListenerContainerFactory(DefaultJmsListenerContainerFactory jmsListenerContainerFactory) {
        jmsListenerContainerFactory.setSessionTransacted(false);
    }
}
