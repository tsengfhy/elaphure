package com.tsengfhy.entry.job;

import com.tsengfhy.elaphure.scheduling.Job;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component("TestJob")
public class TestJob implements Job {

    public static final String VALUE = "test";

    private volatile String value;

    @Override
    public void execute(Map<String, List<String>> parameters) {
        this.setValue(VALUE);
        log.info("TestJob executed");
    }
}
