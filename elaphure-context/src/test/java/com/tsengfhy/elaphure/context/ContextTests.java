package com.tsengfhy.elaphure.context;

import com.tsengfhy.entry.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
class ContextTests {

    @Autowired(required = false)
    private TestComponent testComponent;

    @Test
    void testAutoConfiguration() {
        Assertions.assertNotNull(testComponent, "Components under elaphure root dictionary should be configured automatically, even though app entry is in a different path.");
    }

    @Autowired
    private Environment env;

    private static final String KEY = "test";

    @Test
    void testBuildInProperties() {
        Assertions.assertNotNull(env.getProperty(KEY));
    }
}
