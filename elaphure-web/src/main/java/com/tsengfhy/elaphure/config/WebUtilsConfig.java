package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.env.WebProperties;
import com.tsengfhy.elaphure.utils.ResponseUtils;
import com.tsengfhy.elaphure.utils.WebUtils;
import com.tsengfhy.elaphure.utils.XssUtils;
import com.tsengfhy.elaphure.web.error.ErrorAttributeOptionsConfigurerAdapter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.accept.ContentNegotiationManager;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@AutoConfigureAfter({ErrorMvcAutoConfiguration.class, WebConfig.class})
public class WebUtilsConfig implements InitializingBean {

    @Autowired
    private WebProperties webProperties;

    @Autowired(required = false)
    public void configureWebUtils(ContentNegotiationManager contentNegotiationManager) {
        WebUtils.setContentNegotiationManager(contentNegotiationManager);
    }

    /**
     * As Spring Boot already provides new configuration to support desensitization, just integrate it simply.
     *
     * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController#getErrorAttributeOptions
     */
    @Autowired
    public void configureResponseUtils(ServerProperties serverProperties) {
        ResponseUtils.setOptionsConfigurer(new ErrorAttributeOptionsConfigurerAdapter(serverProperties.getError()));
    }

    public void configureXssUtils() {
        XssUtils.setXss(webProperties.getXss());
    }

    @Override
    public void afterPropertiesSet() {
        configureXssUtils();
    }
}
