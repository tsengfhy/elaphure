package com.tsengfhy.elaphure.config;

import com.google.gson.Gson;
import com.tsengfhy.elaphure.support.DateTimeFormatterGsonBuilderCustomizer;
import com.tsengfhy.elaphure.support.YamlPropertySourceFactory;
import com.tsengfhy.elaphure.utils.JsonUtils;
import com.tsengfhy.elaphure.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration(proxyBeanMethods = false)
@PropertySource(value = "classpath:elaphure-utils.yml", factory = YamlPropertySourceFactory.class, ignoreResourceNotFound = true)
public class UtilsConfig {

    @Autowired
    public void configureMessageUtils(MessageSource messageSource) {
        MessageUtils.setMessageSource(messageSource);
    }

    @Autowired
    public void configureJsonUtils(Gson gson) {
        JsonUtils.setGson(gson);
    }

    @Bean
    public DateTimeFormatterGsonBuilderCustomizer dateTimeFormatterGsonBuilderCustomizer() {
        return new DateTimeFormatterGsonBuilderCustomizer();
    }
}
