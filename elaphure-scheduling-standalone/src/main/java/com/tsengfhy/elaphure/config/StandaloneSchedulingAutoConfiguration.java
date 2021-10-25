package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.constants.Context;
import com.tsengfhy.elaphure.env.StandaloneSchedulingProperties;
import com.tsengfhy.elaphure.scheduling.StandaloneSchedulingJobRegistrar;
import com.tsengfhy.elaphure.scheduling.StandaloneSchedulingJobRegistry;
import com.tsengfhy.elaphure.utils.QuartzUtils;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration(after = QuartzAutoConfiguration.class, before = CommandLineSchedulingAutoConfiguration.class)
@ConditionalOnClass(Scheduler.class)
@ConditionalOnProperty(name = Context.PREFIX + ".scheduling.standalone.enabled", matchIfMissing = true)
@EnableConfigurationProperties(StandaloneSchedulingProperties.class)
class StandaloneSchedulingAutoConfiguration {

    @Autowired
    private StandaloneSchedulingProperties properties;

    @Bean
    @ConditionalOnMissingBean(StandaloneSchedulingJobRegistry.class)
    StandaloneSchedulingJobRegistry jobRegistry() {
        StandaloneSchedulingJobRegistry jobRegistry = new StandaloneSchedulingJobRegistry();
        jobRegistry.setLazyCheck(properties.isLazyCheck());
        jobRegistry.setAutoDelete(properties.isAutoDelete());
        return jobRegistry;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(Scheduler.class)
    static class QuartzUtilsConfiguration {

        @Autowired
        void configureQuartzUtils(Scheduler scheduler) {
            QuartzUtils.setScheduler(scheduler);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class JobRegistrarListenerConfiguration {

        @Autowired
        private StandaloneSchedulingProperties properties;

        @Autowired
        private StandaloneSchedulingJobRegistry jobRegistry;

        @Bean
        StandaloneSchedulingJobRegistrar<ApplicationStartedEvent> applicationStartedJobRegistrar() {
            return new StandaloneSchedulingJobRegistrar<>(jobRegistry, properties) {
            };
        }

        @Bean
        @ConditionalOnClass(RefreshScopeRefreshedEvent.class)
        StandaloneSchedulingJobRegistrar<RefreshScopeRefreshedEvent> refreshScopeRefreshedJobRegistrar() {
            return new StandaloneSchedulingJobRegistrar<>(jobRegistry, properties) {
            };
        }
    }
}
