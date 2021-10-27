package com.tsengfhy.elaphure.scheduling;

import com.alibaba.schedulerx.shade.org.apache.commons.lang.StringUtils;
import com.alibaba.schedulerx.worker.domain.JobContext;
import com.alibaba.schedulerx.worker.processor.JobProcessor;
import com.alibaba.schedulerx.worker.processor.ProcessResult;
import com.tsengfhy.elaphure.constants.ParameterChar;
import org.springframework.scheduling.SchedulingException;

import java.util.*;
import java.util.stream.Collectors;

public class JobDelegate extends AbstractJobExecutor implements JobProcessor {

    @Override
    public void preProcess(JobContext context) {

    }

    @Override
    public ProcessResult process(JobContext context) {
        Map<String, List<String>> parameters = resolveParameters(context.getJobParameters());
        Optional.ofNullable(parameters.get(DEFAULT_JOB_NAME_KEY))
                .orElseThrow(() -> new SchedulingException("No job specified"))
                .stream()
                .findFirst()
                .filter(StringUtils::isNotBlank)
                .map(this::resolveJob)
                .orElseThrow(() -> new SchedulingException("No job specified"))
                .execute(parameters);
        return new ProcessResult(true);
    }

    @Override
    public ProcessResult postProcess(JobContext context) {
        return null;
    }

    @Override
    public void kill(JobContext context) {

    }

    /**
     * Parameters format:
     * jobName=testJob;key=value1,value2
     */
    private Map<String, List<String>> resolveParameters(String parameterString) {
        return Optional.ofNullable(parameterString)
                .map(StringUtils::deleteWhitespace)
                .map(str -> str.split(ParameterChar.SEMICOLON.getValue()))
                .map(Arrays::asList)
                .orElseGet(ArrayList::new)
                .stream()
                .map(item -> item.split(ParameterChar.EQUAL.getValue()))
                .collect(Collectors.toUnmodifiableMap(
                        pair -> pair[0],
                        pair -> Optional.ofNullable(pair[1])
                                .map(value -> value.split(ParameterChar.COMMA.getValue()))
                                .map(Arrays::asList)
                                .orElseGet(Collections::emptyList))
                );
    }
}
