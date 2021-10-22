package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.constants.Context;
import com.tsengfhy.elaphure.env.CommandLineSchedulingProperties;
import com.tsengfhy.elaphure.scheduling.AbstractJobExecutor;
import com.tsengfhy.elaphure.scheduling.CommandLineJobExecutor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnMissingBean(AbstractJobExecutor.class)
@ConditionalOnProperty(name = Context.PREFIX + ".scheduling.command-line.enabled", matchIfMissing = true)
@EnableConfigurationProperties({CommandLineSchedulingProperties.class})
class CommandLineSchedulingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CommandLineJobExecutor.class)
    CommandLineJobExecutor jobExecutor() {
        return new CommandLineJobExecutor();
    }
}
