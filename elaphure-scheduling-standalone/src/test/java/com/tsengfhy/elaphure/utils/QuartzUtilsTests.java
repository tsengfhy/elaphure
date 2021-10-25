package com.tsengfhy.elaphure.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class QuartzUtilsTests {

    @Autowired
    private Scheduler scheduler;

    @Test
    void test() throws Exception {
        JobKey jobKey = JobKey.jobKey("testJob");
        String cron = "/5 * * * * ?";

        Assertions.assertFalse(QuartzUtils.addJob(jobKey, cron, null), "Job added via framework should exists");
        Assertions.assertTrue(QuartzUtils.updateJob(jobKey, cron));
        Assertions.assertTrue(scheduler.getTriggersOfJob(jobKey)
                .stream()
                .filter(trigger -> trigger instanceof CronTrigger)
                .map(trigger -> ((CronTrigger) trigger).getCronExpression())
                .anyMatch(cron::equals));
        Assertions.assertTrue(QuartzUtils.deleteJob(jobKey));
        Assertions.assertFalse(QuartzUtils.selectJobs(null).keySet().stream().map(JobDetail::getKey).collect(Collectors.toSet()).contains(jobKey));
    }
}
