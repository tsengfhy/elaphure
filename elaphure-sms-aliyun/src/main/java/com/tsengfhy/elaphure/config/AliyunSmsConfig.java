package com.tsengfhy.elaphure.config;

import com.alibaba.cloud.spring.boot.context.env.AliCloudProperties;
import com.alibaba.cloud.spring.boot.sms.ISmsService;
import com.alibaba.cloud.spring.boot.sms.autoconfigure.SmsAutoConfiguration;
import com.alibaba.cloud.spring.boot.sms.autoconfigure.SmsContextAutoConfiguration;
import com.alibaba.cloud.spring.boot.sms.env.SmsProperties;
import com.tsengfhy.elaphure.env.ContextProperties;
import com.tsengfhy.elaphure.sms.AliyunSmsTemplate;
import com.tsengfhy.elaphure.sms.SmsTemplate;
import com.tsengfhy.elaphure.sms.annotation.SmsListenerAnnotationBeanPostProcessor;
import com.tsengfhy.elaphure.sms.config.AliyunSmsListenerContainerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@ConditionalOnProperty(name = "alibaba.cloud.sms.enabled", matchIfMissing = true)
@AutoConfigureAfter({SmsContextAutoConfiguration.class})
@AutoConfigureBefore({SmsAutoConfiguration.class})
public class AliyunSmsConfig implements InitializingBean {

    @Autowired
    private ContextProperties contextProperties;

    @Autowired
    private com.tsengfhy.elaphure.env.SmsProperties properties;

    @Autowired
    private AliCloudProperties aliCloudProperties;

    @Autowired
    private SmsProperties smsProperties;

    @Override
    public void afterPropertiesSet() {
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(Optional.ofNullable(properties.getAccessKey()).orElseGet(contextProperties::getAccessKey)).to(aliCloudProperties::setAccessKey);
        mapper.from(Optional.ofNullable(properties.getSecretKey()).orElseGet(contextProperties::getSecretKey)).to(aliCloudProperties::setSecretKey);
        mapper.from(properties.getReplyDestination()).to(smsProperties::setUpQueueName);
    }

    @Bean
    @ConditionalOnMissingBean
    public SmsTemplate smsTemplate(ISmsService smsService) {
        return AliyunSmsTemplate.builder().smsService(smsService).build();
    }

    @Bean(SmsListenerAnnotationBeanPostProcessor.DEFAULT_SMS_LISTENER_CONTAINER_FACTORY_BEAN_NAME)
    @ConditionalOnMissingBean(name = SmsListenerAnnotationBeanPostProcessor.DEFAULT_SMS_LISTENER_CONTAINER_FACTORY_BEAN_NAME)
    public AliyunSmsListenerContainerFactory smsListenerContainerFactory(ISmsService smsService) {
        return AliyunSmsListenerContainerFactory.builder().smsService(smsService).build();
    }
}
