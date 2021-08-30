package com.tsengfhy.elaphure.sms.config;

import com.tsengfhy.elaphure.sms.listener.AbstractSmsListenerContainer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link AbstractSmsListenerContainer} will not be injected as a spring bean, but will also run as a bean with lifecycle.
 */
@Slf4j
public class SmsListenerEndpointRegistry implements DisposableBean, SmartLifecycle, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    @Setter
    private ApplicationContext applicationContext;

    private boolean contextRefreshed;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            this.contextRefreshed = true;
        }
    }

    private final Map<String, AbstractSmsListenerContainer> listenerContainers = new ConcurrentHashMap<>();

    public void registerListenerContainer(AbstractSmsListenerEndpoint endpoint, AbstractSmsListenerContainerFactory<?> factory, boolean startImmediately) {
        Assert.notNull(endpoint, "Endpoint must not be null");
        Assert.notNull(factory, "Factory must not be null");
        String id = endpoint.getId();
        Assert.hasText(id, "Endpoint id must be set");

        synchronized (this.listenerContainers) {
            if (this.listenerContainers.containsKey(id)) {
                throw new IllegalStateException("Another endpoint is already registered with id '" + id + "'");
            }
            AbstractSmsListenerContainer container = createListenerContainer(endpoint, factory);
            this.listenerContainers.put(id, container);
            if (startImmediately) {
                startIfNecessary(container);
            }
        }
    }

    protected AbstractSmsListenerContainer createListenerContainer(AbstractSmsListenerEndpoint endpoint, AbstractSmsListenerContainerFactory<?> factory) {

        AbstractSmsListenerContainer listenerContainer = factory.createListenerContainer(endpoint);

        if (listenerContainer instanceof InitializingBean) {
            try {
                ((InitializingBean) listenerContainer).afterPropertiesSet();
            } catch (Exception ex) {
                throw new BeanInitializationException("Failed to initialize message listener container", ex);
            }
        }

        int containerPhase = listenerContainer.getPhase();
        if (containerPhase < Integer.MAX_VALUE) {  // a custom phase value
            if (this.phase < Integer.MAX_VALUE && this.phase != containerPhase) {
                throw new IllegalStateException("Encountered phase mismatch between container factory definitions: " + this.phase + " vs " + containerPhase);
            }
            this.phase = containerPhase;
        }

        return listenerContainer;
    }

    private void startIfNecessary(AbstractSmsListenerContainer listenerContainer) {
        if (this.contextRefreshed || listenerContainer.isAutoStartup()) {
            listenerContainer.start();
        }
    }

    @Nullable
    public AbstractSmsListenerContainer getListenerContainer(String id) {
        Assert.notNull(id, "Container identifier must not be null");
        return this.listenerContainers.get(id);
    }

    public Set<String> getListenerContainerIds() {
        return Collections.unmodifiableSet(this.listenerContainers.keySet());
    }

    public Collection<AbstractSmsListenerContainer> getListenerContainers() {
        return Collections.unmodifiableCollection(this.listenerContainers.values());
    }

    private int phase = DEFAULT_PHASE;

    @Override
    public int getPhase() {
        return this.phase;
    }

    @Override
    public void start() {
        getListenerContainers().forEach(this::startIfNecessary);
    }

    @Override
    public void stop() {
        getListenerContainers().forEach(AbstractSmsListenerContainer::stop);
    }

    @Override
    public void stop(Runnable callback) {
        Runnable aggregatingCallback = new AggregatingCallback(listenerContainers.size(), callback);
        getListenerContainers().forEach(listenerContainer -> listenerContainer.stop(aggregatingCallback));
    }

    @Override
    public boolean isRunning() {
        return getListenerContainers().stream().anyMatch(AbstractSmsListenerContainer::isRunning);
    }

    @Override
    public void destroy() {
        for (AbstractSmsListenerContainer listenerContainer : getListenerContainers()) {
            if (listenerContainer instanceof DisposableBean) {
                try {
                    ((DisposableBean) listenerContainer).destroy();
                } catch (Throwable ex) {
                    log.warn("Failed to destroy message listener container", ex);
                }
            }
        }
    }

    private static class AggregatingCallback implements Runnable {

        private final AtomicInteger count;

        private final Runnable finishCallback;

        public AggregatingCallback(int count, Runnable finishCallback) {
            this.count = new AtomicInteger(count);
            this.finishCallback = finishCallback;
        }

        @Override
        public void run() {
            if (this.count.decrementAndGet() == 0) {
                this.finishCallback.run();
            }
        }
    }
}
