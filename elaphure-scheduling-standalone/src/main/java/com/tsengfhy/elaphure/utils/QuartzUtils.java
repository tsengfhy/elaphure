package com.tsengfhy.elaphure.utils;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public final class QuartzUtils {

    @Setter
    private static Scheduler scheduler;

    public static Scheduler getScheduler() {
        return Optional.ofNullable(scheduler).orElseThrow(UnsupportedOperationException::new);
    }

    @SneakyThrows(SchedulerException.class)
    public static Map<JobDetail, List<? extends Trigger>> selectJobs(@Nullable String groupName) {
        Scheduler scheduler = getScheduler();
        Map<JobDetail, List<? extends Trigger>> result = new HashMap<>();
        for (JobKey jobKey : scheduler.getJobKeys(Optional.ofNullable(groupName).map(GroupMatcher::jobGroupEquals).orElseGet(GroupMatcher::anyJobGroup))) {
            result.put(scheduler.getJobDetail(jobKey), scheduler.getTriggersOfJob(jobKey));
        }
        return Collections.unmodifiableMap(result);
    }

    @SneakyThrows(SchedulerException.class)
    public static boolean addJob(JobKey jobKey, String cron, @Nullable Map<String, String> parameters) {
        Scheduler scheduler = getScheduler();
        if (scheduler.checkExists(jobKey)) {
            return false;
        }

        JobDetail jobDetail = JobBuilder.newJob()
                .withIdentity(jobKey)
                .ofType(com.tsengfhy.elaphure.scheduling.JobDelegate.class)
                .usingJobData(Optional.ofNullable(parameters).map(JobDataMap::new).orElseGet(JobDataMap::new))
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing())
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
        return true;
    }

    /**
     * Simply support cron change.
     * For parameters change, please delete job and re-add.
     */
    @SneakyThrows(SchedulerException.class)
    public static boolean updateJob(JobKey jobKey, String cron) {
        Scheduler scheduler = getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup());
        if (!scheduler.checkExists(jobKey)) {
            return false;
        } else if (!scheduler.checkExists(triggerKey)) {
            log.warn("Only support default trigger change");
            return false;
        }

        if (Optional.of(scheduler.getTrigger(triggerKey))
                .filter(trigger -> trigger instanceof CronTrigger)
                .filter(trigger -> ((CronTrigger) trigger).getCronExpression().equals(cron))
                .isPresent()) {
            return true;
        }

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing())
                .build();
        scheduler.rescheduleJob(triggerKey, trigger);
        return true;
    }

    @SneakyThrows(SchedulerException.class)
    public static boolean deleteJob(JobKey jobKey) {
        Scheduler scheduler = getScheduler();
        if (!scheduler.checkExists(jobKey)) {
            return false;
        }

        scheduler.pauseJob(jobKey);
        boolean result = scheduler.unscheduleJobs(scheduler.getTriggersOfJob(jobKey).stream().map(Trigger::getKey).collect(Collectors.toList()));
        // For non-persistent jobs, they will be deleted when unschedule.
        scheduler.deleteJob(jobKey);
        return result;
    }
}
