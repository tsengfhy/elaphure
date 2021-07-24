package com.tsengfhy.elaphure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsengfhy.elaphure.utils.JsonUtils;
import com.tsengfhy.elaphure.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration(after = JacksonAutoConfiguration.class)
class UtilsAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    static class MessageUtilsConfiguration {

        @Autowired
        void configureMessageUtils(MessageSource messageSource) {
            MessageUtils.setMessageSource(messageSource);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(ObjectMapper.class)
    static class JsonUtilsConfiguration {

        @Autowired
        void configureJsonUtils(ObjectMapper mapper) {
            JsonUtils.setMapper(mapper);
        }
    }
}
