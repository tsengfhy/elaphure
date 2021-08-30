package com.tsengfhy.elaphure.sms.listener;

import com.alibaba.cloud.spring.boot.sms.ISmsService;
import com.tsengfhy.elaphure.sms.domain.SmsMessage;
import com.tsengfhy.elaphure.sms.mapper.SmsMessageMapper;
import com.tsengfhy.elaphure.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.function.Function;

public class AliyunSmsListenerContainer extends AbstractSmsListenerContainer {

    @Setter
    private ISmsService smsService;

    private Function<SmsMessage, Boolean> listener;

    @Getter
    private volatile boolean running;

    @Override
    public void setupListener(Function<SmsMessage, Boolean> listener) {
        this.listener = listener;
    }

    @Override
    public void start() {
        synchronized (this) {
            if (!this.isRunning()) {
                smsService.startSmsUpMessageListener(message -> listener.apply(SmsMessageMapper.INSTANCE.fromReply(JsonUtils.fromJson(message.getMessageBody(), HashMap.class))));
                this.running = true;
            }
        }
    }

    @Override
    public void stop() {
        synchronized (this) {
            this.running = false;
        }
    }
}
