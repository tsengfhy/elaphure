package com.tsengfhy.elaphure.scheduling;

import com.tsengfhy.elaphure.exception.NoJobSpecifiedException;
import com.tsengfhy.elaphure.utils.QuartzUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.*;
import java.util.stream.Collectors;

@DisallowConcurrentExecution
public class DelegatingJob extends AbstractJobExecutor implements org.quartz.Job {

    @Override
    public void execute(JobExecutionContext context) {
        Map<String, List<String>> parameters = resolveParameters(context.getMergedJobDataMap());
        Optional.ofNullable(parameters.get(JOB_NAME_KEY))
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .map(this::resolveJob)
                .orElseThrow(NoJobSpecifiedException::new)
                .execute(parameters);
    }

    private Map<String, List<String>> resolveParameters(JobDataMap jobDataMap) {
        return jobDataMap.keySet().stream().collect(Collectors.toUnmodifiableMap(key -> key, key -> Arrays.asList(jobDataMap.getString(key).split(QuartzUtils.DELIMITER))));
    }
}
