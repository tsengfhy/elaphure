package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.constants.Context;
import com.tsengfhy.elaphure.env.WebProperties;
import com.tsengfhy.elaphure.support.YamlPropertySourceFactory;
import com.tsengfhy.elaphure.web.cors.OrderedCorsFilter;
import com.tsengfhy.elaphure.web.cors.RestCorsProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@PropertySource(value = "classpath:elaphure-web.yml", factory = YamlPropertySourceFactory.class, ignoreResourceNotFound = true)
@EnableConfigurationProperties({WebProperties.class})
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private WebProperties properties;

    /**
     * In order to customize response, using {@link CorsFilter} instead of {@link org.springframework.web.servlet.handler.AbstractHandlerMapping.CorsInterceptor}.
     * When using {@link CorsConfiguration#setAllowedOriginPatterns}, will get response with 'Access-Control-Allow-Origin' header of concrete host.
     *
     * @see org.springframework.web.servlet.handler.AbstractHandlerMapping#getCorsHandlerExecutionChain
     * @see CorsConfiguration#checkOrigin
     */
    @Bean
    @ConditionalOnProperty(name = Context.PREFIX + ".web.cors.enabled", matchIfMissing = true)
    public CorsFilter corsFilter() {
        WebProperties.Cors corsProperties = properties.getCors();
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(corsProperties::getAllowedOrigins).to(config::setAllowedOriginPatterns);
        mapper.from(corsProperties::getAllowedMethods).to(config::setAllowedMethods);
        mapper.from(corsProperties::getAllowedHeaders).to(config::setAllowedHeaders);
        mapper.from(corsProperties::getExposedHeaders).to(config::setExposedHeaders);
        mapper.from(corsProperties::isAllowCredentials).to(config::setAllowCredentials);
        mapper.from(corsProperties::getMaxAge).to(config::setMaxAge);

        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", config);

        CorsFilter filter = new OrderedCorsFilter(corsConfigurationSource);
        filter.setCorsProcessor(new RestCorsProcessor());
        return filter;
    }
}
