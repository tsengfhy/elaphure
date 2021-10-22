package com.tsengfhy.elaphure.scheduling;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.SchedulingException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Setter
public class CommandLineJobExecutor extends AbstractJobExecutor implements ApplicationRunner {

    /**
     * Job orchestration supported.
     * Using as '--jobName=job1 --jobName=job2', and job2 will be run after job1 runs successfully.
     */
    @Override
    public void run(ApplicationArguments args) {
        Map<String, List<String>> parameters = resolveParameters(args);
        Optional.ofNullable(args.getOptionValues(DEFAULT_JOB_NAME_KEY))
                .orElseThrow(() -> new SchedulingException("No job specified"))
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(this::resolveJob)
                //To make sure that all jobs exists
                .collect(Collectors.toCollection(LinkedList::new))
                .forEach(job -> job.execute(parameters));
    }

    private Map<String, List<String>> resolveParameters(ApplicationArguments args) {
        return args.getOptionNames().stream().collect(Collectors.toUnmodifiableMap(optionName -> optionName, args::getOptionValues));
    }
}
