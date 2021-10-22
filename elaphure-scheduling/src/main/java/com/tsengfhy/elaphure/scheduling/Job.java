package com.tsengfhy.elaphure.scheduling;

import org.springframework.scheduling.SchedulingException;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface Job {
    void execute(Map<String, List<String>> parameters) throws SchedulingException;
}
