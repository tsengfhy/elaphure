package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.gateway.GatewaySwaggerResourcesProvider;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.oas.web.SpecGeneration;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "springfox.documentation.enabled", havingValue = "true", matchIfMissing = true)
public class OpenApiConfig implements ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    @Value(SpecGeneration.OPEN_API_SPECIFICATION_PATH)
    private String openApiPath;

    @Bean
    @Primary
    public SwaggerResourcesProvider swaggerResourcesProvider(RouteLocator routeLocator, ReactiveDiscoveryClient reactiveDiscoveryClient, DiscoveryLocatorProperties discoveryLocatorProperties) {
        String routeIdPrefix = Optional.ofNullable(discoveryLocatorProperties.getRouteIdPrefix()).orElseGet(() -> reactiveDiscoveryClient.getClass().getSimpleName() + "_");

        return GatewaySwaggerResourcesProvider.builder()
                .routeLocator(routeLocator)
                .routeIdPrefix(routeIdPrefix)
                .applicationName(applicationContext.getId())
                .openApiPath(openApiPath)
                .build();
    }
}
