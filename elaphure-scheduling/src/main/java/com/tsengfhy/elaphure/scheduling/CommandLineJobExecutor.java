package com.tsengfhy.elaphure.scheduling;

import com.tsengfhy.elaphure.exception.NoJobSpecifiedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.*;
import java.util.stream.Collectors;

public class CommandLineJobExecutor extends AbstractJobExecutor implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, List<String>> parameters = resolveParameters(args);
        List<Job> jobs = Optional.ofNullable(parameters.get(JOB_NAME_KEY))
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(this::resolveJob)
                .collect(Collectors.toCollection(LinkedList::new));

        if (jobs.isEmpty()) {
            throw new NoJobSpecifiedException();
        }

        try {
            for (Job job : jobs) {
                job.execute(parameters);
            }
        } finally {
            Thread.sleep(30 * 1000);
        }
    }

    private Map<String, List<String>> resolveParameters(ApplicationArguments args) {
        return args.getOptionNames().stream().collect(Collectors.toUnmodifiableMap(optionName -> optionName, args::getOptionValues));
    }
}
