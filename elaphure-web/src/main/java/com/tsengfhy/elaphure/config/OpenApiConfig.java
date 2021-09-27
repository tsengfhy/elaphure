package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.env.WebProperties;
import lombok.Setter;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "springfox.documentation.enabled", havingValue = "true", matchIfMissing = true)
public class OpenApiConfig implements ApplicationContextAware, BeanNameAware, InitializingBean {

    @Autowired
    private WebProperties properties;

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private String beanName;

    @Override
    public void afterPropertiesSet() {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        String docketBeanName = Docket.class.getSimpleName();

        properties.getOpenApi().getGroupMap().forEach((groupName, basePackages) -> {
            beanFactory.registerBeanDefinition(groupName + docketBeanName, beanDefinition(groupName, basePackages));
        });

        if (properties.getOpenApi().getGroupMap().isEmpty() || !properties.getOpenApi().getBasePackages().isEmpty()) {
            beanFactory.registerBeanDefinition(docketBeanName.toLowerCase(), beanDefinition(null, properties.getOpenApi().getBasePackages()));
        }
    }

    private BeanDefinition beanDefinition(@Nullable String groupName, List<String> basePackages) {
        return BeanDefinitionBuilder.genericBeanDefinition(Docket.class)
                .setFactoryMethodOnBean("docket", beanName)
                .addConstructorArgValue(groupName)
                .addConstructorArgValue(basePackages)
                .getBeanDefinition();
    }

    private Docket docket(@Nullable String groupName, List<String> basePackages) {
        ApiSelectorBuilder builder = new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName(groupName)
                .select();
        basePackages.forEach(basePackage -> builder.apis(RequestHandlerSelectors.basePackage(basePackage)));

        return builder.build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(properties.getOpenApi().getTitle())
                .description(properties.getOpenApi().getDescription())
                .version(properties.getOpenApi().getVersion())
                .termsOfServiceUrl(properties.getOpenApi().getTermsOfService())
                .contact(new Contact(properties.getOpenApi().getContact(), properties.getOpenApi().getUrl(), properties.getOpenApi().getMail()))
                .build();
    }
}
