package com.tsengfhy.entry.job;

import com.tsengfhy.elaphure.scheduling.Job;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component("SecondJob")
public class SecondJob implements Job {

    @Autowired
    private FirstJob firstJob;

    private String value;

    @Override
    public void execute(Map<String, List<String>> parameters) {
        this.setValue(firstJob.getValue());
        log.info("SecondJob executed");
    }
}
