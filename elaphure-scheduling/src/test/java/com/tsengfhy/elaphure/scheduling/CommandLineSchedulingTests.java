package com.tsengfhy.elaphure.scheduling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(args = {"--jobName=firstJob", "--jobName=secondJob"})
public class CommandLineSchedulingTests {

    @Autowired
    private FirstJob firstJob;

    @Autowired
    private SecondJob secondJob;

    @Test
    void testRun() {
        Assertions.assertEquals(firstJob.getValue(), FirstJob.TEST_VALUE);
    }

    @Test
    void testOrchestration() {
        Assertions.assertTrue(secondJob.isResult());
    }
}
