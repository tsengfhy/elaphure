package com.tsengfhy.elaphure.sms.config;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class SmsListenerEndpointRegistrar implements BeanFactoryAware, InitializingBean {

    @Setter
    private BeanFactory beanFactory;

    @Setter
    private SmsListenerEndpointRegistry endpointRegistry;

    @Setter
    @Nullable
    private AbstractSmsListenerContainerFactory<?> containerFactory;

    @Setter
    @Nullable
    private String containerFactoryBeanName;

    private final List<SmsListenerEndpointDescriptor> endpointDescriptors = new ArrayList<>();

    private boolean startImmediately;

    @Override
    public void afterPropertiesSet() {
        registerAllEndpoints();
    }

    protected void registerAllEndpoints() {
        Assert.state(this.endpointRegistry != null, "No SmsListenerEndpointRegistry set");
        synchronized (this.endpointDescriptors) {
            for (SmsListenerEndpointDescriptor descriptor : this.endpointDescriptors) {
                this.endpointRegistry.registerListenerContainer(descriptor.endpoint, resolveContainerFactory(descriptor), false);
            }
            this.startImmediately = true;  // trigger immediate startup
        }
    }

    public void registerEndpoint(AbstractSmsListenerEndpoint endpoint, @Nullable AbstractSmsListenerContainerFactory<?> factory) {
        Assert.notNull(endpoint, "Endpoint must not be null");
        Assert.hasText(endpoint.getId(), "Endpoint id must be set");

        // Factory may be null, we defer the resolution right before actually creating the container
        SmsListenerEndpointDescriptor descriptor = new SmsListenerEndpointDescriptor(endpoint, factory);

        synchronized (this.endpointDescriptors) {
            if (this.startImmediately) {  // register and start immediately
                Assert.state(this.endpointRegistry != null, "No JmsListenerEndpointRegistry set");
                this.endpointRegistry.registerListenerContainer(descriptor.endpoint, resolveContainerFactory(descriptor), true);
            } else {
                this.endpointDescriptors.add(descriptor);
            }
        }
    }

    private AbstractSmsListenerContainerFactory<?> resolveContainerFactory(SmsListenerEndpointDescriptor descriptor) {
        if (descriptor.containerFactory != null) {
            return descriptor.containerFactory;
        } else if (this.containerFactory != null) {
            return this.containerFactory;
        } else if (this.containerFactoryBeanName != null) {
            Assert.state(this.beanFactory != null, "BeanFactory must be set to obtain container factory by bean name");
            // Consider changing this if live change of the factory is required...
            this.containerFactory = this.beanFactory.getBean(this.containerFactoryBeanName, AbstractSmsListenerContainerFactory.class);
            return this.containerFactory;
        } else {
            throw new IllegalStateException("Could not resolve the " + AbstractSmsListenerContainerFactory.class.getSimpleName() + " to use for [" + descriptor.endpoint + "] no factory was given and no default is set.");
        }
    }

    @AllArgsConstructor
    private static class SmsListenerEndpointDescriptor {

        public final AbstractSmsListenerEndpoint endpoint;

        @Nullable
        public final AbstractSmsListenerContainerFactory<?> containerFactory;
    }
}
