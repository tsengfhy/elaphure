package com.tsengfhy.elaphure.scheduling;

import com.tsengfhy.entry.Application;
import com.tsengfhy.entry.job.FirstJob;
import com.tsengfhy.entry.job.SecondJob;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class, args = {"--jobName=FirstJob", "--jobName=SecondJob"})
class CommandLineSchedulingTests {

    @Autowired
    private FirstJob firstJob;

    @Autowired
    private SecondJob secondJob;

    @Test
    void testExecute() {
        Assertions.assertEquals(firstJob.getValue(), FirstJob.VALUE);
    }

    @Test
    void testOrchestration() {
        Assertions.assertEquals(secondJob.getValue(), FirstJob.VALUE);
    }
}
