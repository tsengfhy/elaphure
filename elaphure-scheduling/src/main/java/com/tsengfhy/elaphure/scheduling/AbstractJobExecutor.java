package com.tsengfhy.elaphure.scheduling;

import com.tsengfhy.elaphure.exception.NoSuchJobException;
import lombok.Setter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Setter
public abstract class AbstractJobExecutor implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public static final String JOB_NAME_KEY = "jobName";

    protected Job resolveJob(String jobName) {
        try {
            return applicationContext.getBean(jobName, Job.class);
        } catch (NoSuchBeanDefinitionException e) {
            throw new NoSuchJobException(jobName, e);
        }
    }
}
