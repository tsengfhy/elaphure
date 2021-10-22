package com.tsengfhy.elaphure.scheduling;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
public class SecondJob implements Job {

    @Autowired
    private FirstJob firstJob;

    private boolean result;

    @Override
    public void execute(Map<String, List<String>> parameters) {
        this.setResult(StringUtils.equals(firstJob.getValue(), FirstJob.TEST_VALUE));
        log.info("orchestration check result set");
    }
}
