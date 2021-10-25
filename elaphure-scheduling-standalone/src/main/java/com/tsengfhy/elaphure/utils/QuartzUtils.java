package com.tsengfhy.elaphure.utils;

import com.tsengfhy.elaphure.scheduling.DelegatingJob;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public final class QuartzUtils {

    @Setter
    private static Scheduler scheduler;

    public static final String DELIMITER = "<br>";

    @SneakyThrows(SchedulerException.class)
    public static void pause(@Nullable String name, @Nullable String group) {
        Scheduler scheduler = getScheduler();

        if (!Objects.isNull(name)) {
            scheduler.pauseJob(JobKey.jobKey(name, group));
        } else {
            scheduler.pauseJobs(Optional.ofNullable(group).map(GroupMatcher::jobGroupEquals).orElseGet(GroupMatcher::anyJobGroup));
        }
    }

    @SneakyThrows(SchedulerException.class)
    public static void resume(@Nullable String name, @Nullable String group) {
        Scheduler scheduler = getScheduler();

        if (!Objects.isNull(name)) {
            scheduler.resumeJob(JobKey.jobKey(name, group));
        } else {
            scheduler.resumeJobs(Optional.ofNullable(group).map(GroupMatcher::jobGroupEquals).orElseGet(GroupMatcher::anyJobGroup));
        }
    }

    @SneakyThrows(SchedulerException.class)
    public static Map<String, Map<JobDetail, List<? extends Trigger>>> select(@Nullable String group) {
        Scheduler scheduler = getScheduler();
        List<String> groups = !Objects.isNull(group) ? List.of(group) : scheduler.getJobGroupNames();

        Map<String, Map<JobDetail, List<? extends Trigger>>> groupMap = new HashMap<>();
        for (String groupName : groups) {
            Map<JobDetail, List<? extends Trigger>> jobMap = new HashMap<>();
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals(groupName))) {
                jobMap.put(scheduler.getJobDetail(jobKey), Collections.unmodifiableList(scheduler.getTriggersOfJob(jobKey)));
            }
            groupMap.put(groupName, Collections.unmodifiableMap(jobMap));
        }
        return Collections.unmodifiableMap(groupMap);
    }

    @SneakyThrows(SchedulerException.class)
    public static boolean add(String name, @Nullable String group, String cron, @Nullable Map<String, String> parameters) {
        Scheduler scheduler = getScheduler();
        JobKey jobKey = JobKey.jobKey(name, group);
        if (scheduler.checkExists(jobKey)) {
            return false;
        }

        JobDetail jobDetail = JobBuilder.newJob()
                .withIdentity(jobKey)
                .ofType(DelegatingJob.class)
                .usingJobData(Optional.ofNullable(parameters).map(JobDataMap::new).orElseGet(JobDataMap::new))
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing())
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
        return true;
    }

    @SneakyThrows(SchedulerException.class)
    public static boolean update(String name, @Nullable String group, String cron) {
        Scheduler scheduler = getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
        if (!scheduler.checkExists(triggerKey)) {
            return false;
        }

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing())
                .build();
        scheduler.rescheduleJob(triggerKey, trigger);
        return true;
    }

    @SneakyThrows(SchedulerException.class)
    public static boolean delete(String name, @Nullable String group) {
        Scheduler scheduler = getScheduler();
        JobKey jobKey = JobKey.jobKey(name, group);
        if (!scheduler.checkExists(jobKey)) {
            return false;
        }

        scheduler.pauseJob(jobKey);
        // If the related job does not have any other triggers, and the job is not durable, then the job will also be deleted.
        scheduler.unscheduleJobs(scheduler.getTriggersOfJob(jobKey).stream().map(Trigger::getKey).collect(Collectors.toList()));
        scheduler.deleteJob(jobKey);
        return true;
    }

    @SneakyThrows(SchedulerException.class)
    public static Trigger.TriggerState getState(String name, @Nullable String group) {
        Scheduler scheduler = getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(name, group);

        return scheduler.getTriggerState(triggerKey);
    }

    private static Scheduler getScheduler() {
        return Optional.ofNullable(scheduler).orElseThrow(UnsupportedOperationException::new);
    }
}
