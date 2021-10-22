package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.constants.Context;
import com.tsengfhy.elaphure.env.CommandLineSchedulingProperties;
import com.tsengfhy.elaphure.scheduling.CommandLineJobExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = Context.PREFIX + ".scheduling.command-line.enabled")
@EnableConfigurationProperties({CommandLineSchedulingProperties.class})
public class CommandLineSchedulingConfig {

    @Autowired
    private CommandLineSchedulingProperties properties;

    @Bean
    public CommandLineJobExecutor jobExecutor() {
        return new CommandLineJobExecutor();
    }
}
