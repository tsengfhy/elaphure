package com.tsengfhy.elaphure.sms.config;

import com.alibaba.cloud.spring.boot.sms.ISmsService;
import com.tsengfhy.elaphure.sms.listener.AliyunSmsListenerContainer;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class AliyunSmsListenerContainerFactory extends AbstractSmsListenerContainerFactory<AliyunSmsListenerContainer> {

    private final ISmsService smsService;

    @Override
    protected AliyunSmsListenerContainer createContainerInstance() {
        return new AliyunSmsListenerContainer();
    }

    @Override
    protected void initializeContainer(AliyunSmsListenerContainer instance) {
        instance.setSmsService(this.smsService);
    }
}
