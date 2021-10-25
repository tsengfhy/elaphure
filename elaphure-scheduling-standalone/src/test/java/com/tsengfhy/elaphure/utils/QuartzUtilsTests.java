package com.tsengfhy.elaphure.utils;

import com.tsengfhy.entry.Application;
import org.junit.jupiter.api.*;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest(classes = Application.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuartzUtilsTests {

    @Autowired
    private Scheduler scheduler;

    private static final String NAME = "TestJob";
    private static final String GROUP = JobKey.DEFAULT_GROUP;
    private static final String CRON = "0/5 * * * * ?";

    @Test
    @Order(1)
    void testAdd() {
        Assertions.assertFalse(QuartzUtils.add(NAME, GROUP, CRON, null));
    }

    @Test
    @Order(2)
    void testUpdate() {
        Assertions.assertTrue(QuartzUtils.update(NAME, GROUP, CRON));
        Map<JobDetail, List<? extends Trigger>> jobTriggerMap = QuartzUtils.select(GROUP).get(GROUP);
        Assertions.assertTrue(jobTriggerMap.keySet()
                .stream()
                .filter(item -> item.getKey().getName().equals(NAME))
                .findAny()
                .map(jobTriggerMap::get)
                .map(triggers -> triggers.stream().filter(trigger -> trigger instanceof CronTrigger).map(trigger -> ((CronTrigger) trigger).getCronExpression()).anyMatch(CRON::equals))
                .orElse(false));
    }

    @Test
    @Order(3)
    void testDelete() {
        Assertions.assertTrue(QuartzUtils.delete(NAME, GROUP));
        Assertions.assertFalse(QuartzUtils.select(GROUP).get(GROUP).keySet()
                .stream()
                .map(JobDetail::getKey)
                .map(JobKey::getName)
                .collect(Collectors.toSet())
                .contains(NAME));
    }
}
