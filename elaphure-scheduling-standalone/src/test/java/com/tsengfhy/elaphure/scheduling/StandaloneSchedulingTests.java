package com.tsengfhy.elaphure.scheduling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StandaloneSchedulingTests {

    @Autowired
    private TestJob testJob;

    @Test
    void testRun() {
        Assertions.assertEquals(testJob.getValue(), TestJob.TEST_VALUE);
    }
}
