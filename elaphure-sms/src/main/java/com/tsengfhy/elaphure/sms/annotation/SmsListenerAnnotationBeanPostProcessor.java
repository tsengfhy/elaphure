package com.tsengfhy.elaphure.sms.annotation;

import com.tsengfhy.elaphure.sms.config.*;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Builder
public class SmsListenerAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware, SmartInitializingSingleton {

    private final SmsListenerEndpointRegistrar registrar = new SmsListenerEndpointRegistrar();

    private final SmsListenerEndpointRegistry endpointRegistry;

    public static final String DEFAULT_SMS_LISTENER_CONTAINER_FACTORY_BEAN_NAME = "smsListenerContainerFactory";

    @Builder.Default
    private final String containerFactoryBeanName = DEFAULT_SMS_LISTENER_CONTAINER_FACTORY_BEAN_NAME;

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    private BeanFactory beanFactory;

    private StringValueResolver embeddedValueResolver;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.embeddedValueResolver = new EmbeddedValueResolver((ConfigurableBeanFactory) beanFactory);
        }
        this.registrar.setBeanFactory(beanFactory);
    }

    @Override
    public void afterSingletonsInstantiated() {
        // Remove resolved singleton classes from cache
        this.nonAnnotatedClasses.clear();

        Optional.ofNullable(this.endpointRegistry).ifPresent(this.registrar::setEndpointRegistry);
        this.registrar.setContainerFactoryBeanName(this.containerFactoryBeanName);

        // Actually register all listeners
        this.registrar.afterPropertiesSet();
    }

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass)) {
            Map<Method, Set<SmsListener>> annotatedMethods = MethodIntrospector.selectMethods(targetClass, (Method method) ->
                    Optional.of(AnnotatedElementUtils.getMergedRepeatableAnnotations(method, SmsListener.class))
                            .filter(set -> !set.isEmpty())
                            .orElse(null));
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                log.trace("No @SmsListener annotations found on bean type: {}", targetClass);
            } else {
                annotatedMethods.forEach((method, listeners) -> listeners.forEach(listener -> processSmsListener(listener, method, bean)));
                log.debug(annotatedMethods.size() + " @SmsListener methods processed on bean '" + beanName + "': " + annotatedMethods);
            }
        }
        return bean;
    }

    protected void processSmsListener(SmsListener listener, Method method, Object bean) {
        Method invocableMethod = AopUtils.selectInvocableMethod(method, bean.getClass());

        MethodSmsListenerEndpoint endpoint = MethodSmsListenerEndpoint.builder()
                .bean(bean)
                .method(invocableMethod)
                .id(getEndpointId(listener))
                .build();

        AbstractSmsListenerContainerFactory<?> factory = null;
        String containerFactoryBeanName = resolve(listener.containerFactory());
        if (StringUtils.hasText(containerFactoryBeanName)) {
            Assert.state(this.beanFactory != null, "BeanFactory must be set to obtain container factory by bean name");
            try {
                factory = this.beanFactory.getBean(containerFactoryBeanName, AbstractSmsListenerContainerFactory.class);
            } catch (NoSuchBeanDefinitionException ex) {
                throw new BeanInitializationException("Could not register SMS listener endpoint on [" + method + "], no " + AbstractSmsListenerContainerFactory.class.getSimpleName() + " with id '" + containerFactoryBeanName + "' was found in the application context", ex);
            }
        }

        this.registrar.registerEndpoint(endpoint, factory);
    }

    private static final String GENERATED_ID_PREFIX = "com.tsengfhy.elaphure.sms.listener.SmsListenerContainer#";

    private final AtomicInteger counter = new AtomicInteger();

    private String getEndpointId(SmsListener listener) {
        String endpointId = resolve(listener.id());
        return StringUtils.hasText(endpointId) ? endpointId : GENERATED_ID_PREFIX + this.counter.getAndIncrement();
    }

    private String resolve(String value) {
        return this.embeddedValueResolver != null ? this.embeddedValueResolver.resolveStringValue(value) : value;
    }
}
