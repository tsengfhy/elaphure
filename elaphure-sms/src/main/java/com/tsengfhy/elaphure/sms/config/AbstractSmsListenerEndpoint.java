package com.tsengfhy.elaphure.sms.config;

import com.tsengfhy.elaphure.sms.listener.AbstractSmsListenerContainer;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractSmsListenerEndpoint {

    private final String id;

    public void setupListenerContainer(AbstractSmsListenerContainer listenerContainer) {
        //setup more features here in the future.

        setupMessageListener(listenerContainer);
    }

    protected abstract void setupMessageListener(AbstractSmsListenerContainer listenerContainer);
}
