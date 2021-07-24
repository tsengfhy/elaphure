package com.tsengfhy.elaphure.context;

import com.tsengfhy.elaphure.env.ContextProperties;
import com.tsengfhy.entry.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest(classes = Application.class)
class ContextTests {

    @Autowired(required = false)
    private ContextProperties properties;

    @Test
    void testYamlPropertySource() {
        Assertions.assertEquals(Optional.ofNullable(properties).map(ContextProperties::getAccessKey).orElse(""), "access");
    }
}
