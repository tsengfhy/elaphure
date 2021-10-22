package com.tsengfhy.elaphure.scheduling;

import lombok.Setter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.SchedulingException;

public abstract class AbstractJobExecutor implements ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    public static final String DEFAULT_JOB_NAME_KEY = "jobName";

    protected Job resolveJob(String jobName) {
        try {
            return applicationContext.getBean(jobName, Job.class);
        } catch (NoSuchBeanDefinitionException e) {
            throw new SchedulingException(String.format("Job '%s' not found", jobName));
        }
    }
}
