package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.constant.Context;
import com.tsengfhy.elaphure.env.WebProperties;
import com.tsengfhy.elaphure.web.cors.RestCorsProcessor;
import com.tsengfhy.elaphure.web.servlet.filter.OrderedCorsFilter;
import com.tsengfhy.elaphure.web.servlet.filter.OrderedXssFilter;
import com.tsengfhy.elaphure.web.servlet.filter.XssFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.util.PathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.UrlPathHelper;

import jakarta.servlet.Servlet;

@AutoConfiguration(after = WebMvcAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@EnableConfigurationProperties(WebProperties.class)
class WebAutoConfiguration {

    @Autowired
    private WebProperties properties;

    /**
     * To customize the global {@link CorsConfiguration}, use {@link CorsFilter} instead of {@link org.springframework.web.servlet.handler.AbstractHandlerMapping.CorsInterceptor}.<br>
     * When using {@link CorsConfiguration#setAllowedOriginPatterns}, will get response with 'Access-Control-Allow-Origin' header for the specific host.
     *
     * @see org.springframework.web.servlet.handler.AbstractHandlerMapping#getCorsHandlerExecutionChain
     * @see CorsConfiguration#checkOrigin
     */
    @Bean
    @ConditionalOnMissingBean(CorsFilter.class)
    @ConditionalOnProperty(name = Context.PREFIX + ".web.cors.enabled", matchIfMissing = true)
    OrderedCorsFilter corsFilter(UrlPathHelper urlPathHelper, PathMatcher pathMatcher) {
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(properties.getCors()::getAllowedOriginPatterns).to(config::setAllowedOriginPatterns);
        mapper.from(properties.getCors()::getAllowedMethods).to(config::setAllowedMethods);
        mapper.from(properties.getCors()::getAllowedHeaders).to(config::setAllowedHeaders);
        mapper.from(properties.getCors()::getExposedHeaders).to(config::setExposedHeaders);
        mapper.from(properties.getCors()::isAllowCredentials).to(config::setAllowCredentials);
        mapper.from(properties.getCors()::getMaxAge).to(config::setMaxAge);

        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.setPathMatcher(pathMatcher);
        configSource.setUrlPathHelper(urlPathHelper);
        configSource.registerCorsConfiguration("/**", config);

        OrderedCorsFilter filter = new OrderedCorsFilter(configSource);
        filter.setCorsProcessor(new RestCorsProcessor());
        return filter;
    }

    @Bean
    @ConditionalOnMissingBean(XssFilter.class)
    @ConditionalOnProperty(name = Context.PREFIX + ".web.xss.enabled", matchIfMissing = true)
    OrderedXssFilter xssFilter(UrlPathHelper urlPathHelper, PathMatcher pathMatcher) {
        OrderedXssFilter xssFilter = new OrderedXssFilter();
        xssFilter.setPathMatcher(pathMatcher);
        xssFilter.setUrlPathHelper(urlPathHelper);
        xssFilter.setAllowedPaths(properties.getXss().getAllowedPaths());
        return xssFilter;
    }
}
