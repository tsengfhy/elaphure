package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.constants.Context;
import com.tsengfhy.elaphure.env.WebProperties;
import com.tsengfhy.elaphure.support.YamlPropertySourceFactory;
import com.tsengfhy.elaphure.utils.XssUtils;
import com.tsengfhy.elaphure.web.cors.OrderedCorsFilter;
import com.tsengfhy.elaphure.web.cors.RestCorsProcessor;
import com.tsengfhy.elaphure.web.error.AdvancedErrorAttributes;
import com.tsengfhy.elaphure.web.servlet.ParameterMappingHttpServletRequest;
import com.tsengfhy.elaphure.web.servlet.WhitelistFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.PathMatcher;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@AutoConfigureAfter({MessageSourceAutoConfiguration.class})
@AutoConfigureBefore({ErrorMvcAutoConfiguration.class})
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

    @Bean
    @ConditionalOnProperty(name = Context.PREFIX + ".web.xss.enabled", matchIfMissing = true)
    public WhitelistFilter xssFilter(UrlPathHelper urlPathHelper, PathMatcher pathMatcher) {
        return WhitelistFilter.builder()
                .order(OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER - 90)
                .urlPathHelper(urlPathHelper)
                .pathMatcher(pathMatcher)
                .allowedPaths(properties.getXss().getAllowedPaths())
                .filter(((request, response, filterChain) -> filterChain.doFilter(
                        new ParameterMappingHttpServletRequest(request, XssUtils::process),
                        response
                )))
                .build();
    }

    /**
     * For i18n, based on Accept Header by default, and this is enough.
     *
     * @see org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.EnableWebMvcConfiguration#localeResolver
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * For validation, using ValidationMessages.properties as resource bundle by default, and here if {@link MessageSource} exists, using messages.properties instead.
     *
     * @see org.hibernate.validator.internal.engine.AbstractConfigurationImpl#getDefaultResourceBundleLocator
     */
    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }

    @Bean
    public ErrorAttributes errorAttributes() {
        return new AdvancedErrorAttributes();
    }
}
