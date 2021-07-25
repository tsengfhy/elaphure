package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.env.WebProperties;
import com.tsengfhy.elaphure.utils.ResponseUtils;
import com.tsengfhy.elaphure.utils.XssUtils;
import com.tsengfhy.elaphure.web.error.ErrorAttributeOptionsConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;

@AutoConfiguration(after = {WebAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
class WebUtilsAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(ErrorMvcAutoConfiguration.class)
    static class ResponseUtilsConfiguration {

        /**
         * Since Spring Boot has provided new configuration to support desensitization, just integrate it.
         *
         * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController#getErrorAttributeOptions
         */
        @Autowired
        void configureResponseUtils(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
            ResponseUtils.setErrorAttributeOptionsConfigurer(new ErrorAttributeOptionsConfigurerAdapter(errorAttributes, serverProperties.getError()));
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(WebAutoConfiguration.class)
    static class XssUtilsConfiguration {

        @Autowired
        void configureXssUtils(WebProperties webProperties) {
            XssUtils.setDefaultProcessStrategy(webProperties.getXss().getProcessStrategy());
        }
    }
}
