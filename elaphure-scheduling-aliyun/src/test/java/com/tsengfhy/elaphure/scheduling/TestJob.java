package com.tsengfhy.elaphure.scheduling;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
public class TestJob implements Job {

    public static final String TEST_VALUE = "test";
    private String value;

    @Override
    public void execute(Map<String, List<String>> parameters) {
        this.setValue(TEST_VALUE);
        log.info("test value set");
    }
}
