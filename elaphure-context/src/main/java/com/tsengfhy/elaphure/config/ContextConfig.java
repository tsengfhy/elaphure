package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.constants.Context;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ComponentScan(basePackages = Context.BASE_PACKAGE)
public class ContextConfig {
}
