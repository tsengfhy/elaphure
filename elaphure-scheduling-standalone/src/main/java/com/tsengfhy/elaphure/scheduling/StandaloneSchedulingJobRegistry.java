package com.tsengfhy.elaphure.scheduling;

import com.tsengfhy.elaphure.env.StandaloneSchedulingProperties;
import com.tsengfhy.elaphure.util.QuartzUtils;
import lombok.Setter;
import org.quartz.*;

import java.util.*;
import java.util.stream.Collectors;

@Setter
public class StandaloneSchedulingJobRegistry extends AbstractJobExecutor {

    private boolean lazyCheck = false;
    private boolean autoDelete = true;

    public void registerJobs(Map<String, Map<String, StandaloneSchedulingProperties.Job>> jobs) {
        Map<String, Map<JobDetail, List<? extends Trigger>>> existingGroupMap = QuartzUtils.select(null);
        Map<String, Map<String, JobDetail>> existingJobDetailMap = existingGroupMap.keySet()
                .stream()
                .collect(Collectors.toMap(group -> group, group -> existingGroupMap.get(group).keySet().stream().collect(Collectors.toMap(jobDetail -> jobDetail.getKey().getName(), jobDetail -> jobDetail))));
        Map<JobDetail, List<? extends Trigger>> existingJobTriggerMap = existingGroupMap.values().stream().reduce(new HashMap<>(), (map, jobDetailMap) -> {
            map.putAll(jobDetailMap);
            return map;
        });

        if (!lazyCheck) {
            jobs.forEach((group, jobMap) -> {
                jobMap.forEach((name, job) -> {
                    String jobName = Optional.ofNullable(job.getAlias()).orElse(name);
                    this.resolveJob(jobName);
                });
            });
        }

        jobs.forEach((group, jobMap) -> {
            jobMap.forEach((name, job) -> {
                String jobName = Optional.ofNullable(job.getAlias()).orElse(name);
                String cron = job.getCron();
                Map<String, String> parameters = job.getParameters()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.join(QuartzUtils.DELIMITER, entry.getValue())));
                parameters.put(AbstractJobExecutor.JOB_NAME_KEY, jobName);

                JobDetail jobDetail = Optional.ofNullable(existingJobDetailMap.get(group)).map(jobDetailMap -> jobDetailMap.get(name)).orElse(null);
                if (Objects.isNull(jobDetail)) {
                    QuartzUtils.add(name, group, cron, parameters);
                } else if (!jobDetail.getJobDataMap().equals(new JobDataMap(parameters))) {
                    // parameters update
                    QuartzUtils.delete(name, group);
                    QuartzUtils.add(name, group, cron, parameters);
                } else if (existingJobTriggerMap.get(jobDetail)
                        .stream()
                        .filter(trigger -> trigger.getKey().equals(TriggerKey.triggerKey(name, group)))
                        .anyMatch(trigger -> !(trigger instanceof CronTrigger) || !((CronTrigger) trigger).getCronExpression().equals(cron))) {
                    // cron update
                    QuartzUtils.update(name, group, cron);
                }
            });
        });

        if (autoDelete) {
            existingJobDetailMap.forEach((group, jobDetailMap) -> {
                jobDetailMap.forEach((name, jobDetail) -> {
                    if (Optional.ofNullable(jobs.get(group)).map(jobMap -> jobMap.get(name)).isEmpty()) {
                        QuartzUtils.delete(name, group);
                    }
                });
            });
        }
    }
}
