package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.env.StandaloneSchedulingProperties;
import com.tsengfhy.elaphure.scheduling.StandaloneJobRegistrar;
import com.tsengfhy.elaphure.utils.QuartzUtils;
import org.quartz.CronExpression;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.SchedulingException;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AutoConfigureAfter({QuartzAutoConfiguration.class})
@EnableConfigurationProperties({StandaloneSchedulingProperties.class})
public class StandaloneSchedulingConfig {

    @Autowired
    private StandaloneSchedulingProperties properties;

    @Bean
    public StandaloneJobRegistrar jobRegistrar() {
        StandaloneJobRegistrar jobRegistrar = new StandaloneJobRegistrar();
        jobRegistrar.setLazyCheck(properties.isLazyCheck());
        jobRegistrar.setAutoDelete(properties.isAutoDelete());
        return jobRegistrar;
    }

    @EventListener({ApplicationStartedEvent.class})
    public void onApplicationStarted() {
        registerAllJobs();
    }

    /**
     * Support update job schedule via config server.
     */
    @EventListener({RefreshScopeRefreshedEvent.class})
    public void onScopeRefreshed() {
        registerAllJobs();
    }

    protected void registerAllJobs() {
        Map<JobKey, StandaloneSchedulingProperties.Job> jobMap = new HashMap<>();
        properties.getJobs().forEach((groupName, map) ->
                map.forEach((jobName, job) -> {
                    if (!CronExpression.isValidExpression(job.getCron())) {
                        throw new SchedulingException(String.format("Wrong cron format of job '%s'", jobName));
                    }
                    jobMap.put(JobKey.jobKey(jobName, groupName), job);
                }));

        jobRegistrar().registerAllJobs(jobMap);
    }

    @Autowired
    public void configureQuartzUtils(Scheduler scheduler) {
        QuartzUtils.setScheduler(scheduler);
    }
}
