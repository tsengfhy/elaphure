package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.openapi.GatewaySwaggerUiConfigParameters;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.webflux.ui.SwaggerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@AutoConfiguration(after = {GatewayAutoConfiguration.class, SpringDocConfiguration.class}, before = SwaggerConfig.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", matchIfMissing = true)
@ConditionalOnBean({SpringDocConfiguration.class})
class GatewayOpenAPIAutoConfiguration {

    @Bean
    @ConditionalOnBean({RouteLocator.class})
    SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfig, SpringDocConfigProperties springDocConfigProperties, @Lazy RouteLocator routeLocator) {
        return new GatewaySwaggerUiConfigParameters(swaggerUiConfig, springDocConfigProperties, routeLocator);
    }
}
