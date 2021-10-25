package com.tsengfhy.elaphure.scheduling;

import com.tsengfhy.entry.Application;
import com.tsengfhy.entry.job.TestJob;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
class StandaloneSchedulingTests {

    @Autowired
    private TestJob testJob;

    @Test
    void testRun() throws Exception {
        Thread.sleep(2000L);
        Assertions.assertEquals(testJob.getValue(), TestJob.VALUE);
    }
}
