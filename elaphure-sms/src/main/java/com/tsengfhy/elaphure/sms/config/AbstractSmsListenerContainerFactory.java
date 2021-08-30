package com.tsengfhy.elaphure.sms.config;

import com.tsengfhy.elaphure.sms.listener.AbstractSmsListenerContainer;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.util.Optional;

@SuperBuilder
public abstract class AbstractSmsListenerContainerFactory<C extends AbstractSmsListenerContainer> {

    @Nullable
    private final Integer phase;

    @Nullable
    private final Boolean autoStartup;

    public C createListenerContainer(AbstractSmsListenerEndpoint endpoint) {
        C instance = createContainerInstance();
        Optional.ofNullable(this.phase).ifPresent(instance::setPhase);
        Optional.ofNullable(this.autoStartup).ifPresent(instance::setAutoStartup);

        initializeContainer(instance);
        endpoint.setupListenerContainer(instance);

        return instance;
    }

    protected abstract C createContainerInstance();

    protected void initializeContainer(C instance) {
    }
}
