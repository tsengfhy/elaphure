package com.tsengfhy.elaphure.scheduling;

import com.tsengfhy.elaphure.constants.ParameterChar;
import com.tsengfhy.elaphure.env.StandaloneSchedulingProperties;
import com.tsengfhy.elaphure.utils.QuartzUtils;
import lombok.Setter;
import org.quartz.*;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.SchedulingException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Setter
public class StandaloneJobRegistrar implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private boolean lazyCheck = false;
    private boolean autoDelete = true;

    public void registerAllJobs(Map<JobKey, StandaloneSchedulingProperties.Job> jobMap) {
        Map<JobDetail, List<? extends Trigger>> existsJobs = QuartzUtils.selectJobs(null);
        Map<JobKey, JobDetail> existsJobKeyMap = existsJobs.keySet().stream().collect(Collectors.toMap(JobDetail::getKey, jobDetail -> jobDetail));
        jobMap.forEach((jobKey, job) -> {
            String jobName = Optional.ofNullable(job.getAlias()).orElse(jobKey.getName());
            String cron = job.getCron();
            Map<String, String> parameters = job.getParameters()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.join(ParameterChar.COMMA.getValue(), entry.getValue())));
            parameters.put(AbstractJobExecutor.DEFAULT_JOB_NAME_KEY, jobName);
            if (!existsJobKeyMap.containsKey(jobKey)) {
                if (!lazyCheck) {
                    try {
                        applicationContext.getBean(jobName, Job.class);
                    } catch (NoSuchBeanDefinitionException e) {
                        throw new SchedulingException(String.format("Job '%s' not found", jobName));
                    }
                }
                QuartzUtils.addJob(jobKey, cron, parameters);
            } else if (!existsJobKeyMap.get(jobKey).getJobDataMap().equals(new JobDataMap(parameters))) {
                // parameters update
                QuartzUtils.deleteJob(jobKey);
                QuartzUtils.addJob(jobKey, cron, parameters);
            } else if (existsJobs.get(existsJobKeyMap.get(jobKey))
                    .stream()
                    .filter(trigger -> trigger.getKey().equals(TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup())))
                    .anyMatch(trigger -> !(trigger instanceof CronTrigger) || !((CronTrigger) trigger).getCronExpression().equals(cron))) {
                // cron update
                QuartzUtils.updateJob(jobKey, cron);
            }
        });

        if (autoDelete) {
            existsJobKeyMap.keySet().stream().filter(jobKey -> !jobMap.containsKey(jobKey)).forEach(QuartzUtils::deleteJob);
        }
    }
}
