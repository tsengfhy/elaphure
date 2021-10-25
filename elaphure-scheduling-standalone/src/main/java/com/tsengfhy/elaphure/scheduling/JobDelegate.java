package com.tsengfhy.elaphure.scheduling;

import com.tsengfhy.elaphure.constants.ParameterChar;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.SchedulingException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@DisallowConcurrentExecution
public class JobDelegate extends AbstractJobExecutor implements org.quartz.Job {

    @Override
    public void execute(JobExecutionContext context) {
        Map<String, List<String>> parameters = resolveParameters(context.getMergedJobDataMap());
        Optional.ofNullable(parameters.get(DEFAULT_JOB_NAME_KEY))
                .orElseThrow(() -> new SchedulingException("No job specified"))
                .stream()
                .findFirst()
                .filter(StringUtils::isNotBlank)
                .map(this::resolveJob)
                .orElseThrow(() -> new SchedulingException("No job specified"))
                .execute(parameters);
    }

    private Map<String, List<String>> resolveParameters(JobDataMap jobDataMap) {
        return jobDataMap.keySet().stream().collect(Collectors.toUnmodifiableMap(key -> key, key -> Arrays.asList(jobDataMap.getString(key).split(ParameterChar.COMMA.getValue()))));
    }
}
