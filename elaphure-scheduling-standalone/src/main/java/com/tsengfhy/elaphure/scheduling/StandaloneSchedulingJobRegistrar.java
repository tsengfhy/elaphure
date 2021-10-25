package com.tsengfhy.elaphure.scheduling;

import com.tsengfhy.elaphure.env.StandaloneSchedulingProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

@AllArgsConstructor
public abstract class StandaloneSchedulingJobRegistrar<E extends ApplicationEvent> implements ApplicationListener<E> {

    private StandaloneSchedulingJobRegistry jobRegistry;
    private StandaloneSchedulingProperties properties;

    @Override
    public void onApplicationEvent(E event) {
        jobRegistry.registerJobs(properties.getJobs());
    }
}
