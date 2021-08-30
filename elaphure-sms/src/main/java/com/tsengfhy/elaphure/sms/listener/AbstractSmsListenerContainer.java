package com.tsengfhy.elaphure.sms.listener;

import com.tsengfhy.elaphure.sms.domain.SmsMessage;
import lombok.Data;
import org.springframework.context.SmartLifecycle;

import java.util.function.Function;

@Data
public abstract class AbstractSmsListenerContainer implements SmartLifecycle {

    private boolean autoStartup = true;

    private int phase = DEFAULT_PHASE;

    public abstract void setupListener(Function<SmsMessage, Boolean> listener);
}
