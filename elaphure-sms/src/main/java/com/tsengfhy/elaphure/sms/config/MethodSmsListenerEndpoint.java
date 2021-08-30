package com.tsengfhy.elaphure.sms.config;

import com.tsengfhy.elaphure.sms.exception.ListenerExecutionFailedException;
import com.tsengfhy.elaphure.sms.listener.AbstractSmsListenerContainer;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
@SuperBuilder
public class MethodSmsListenerEndpoint extends AbstractSmsListenerEndpoint {

    private final Object bean;
    private final Method method;

    @Override
    protected void setupMessageListener(AbstractSmsListenerContainer listenerContainer) {
        listenerContainer.setupListener(message -> {
            try {
                Object result = method.invoke(bean, message);
                return result instanceof Boolean ? (Boolean) result : true;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ListenerExecutionFailedException("Listener method could not be invoked with incoming message", e);
            }
        });
    }
}
