package com.tsengfhy.elaphure.env;

import com.tsengfhy.elaphure.constants.Context;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = Context.PREFIX + ".scheduling.standalone")
public class StandaloneSchedulingProperties {

    @NestedConfigurationProperty
    private Map<String, Map<String, Job>> jobs = new HashMap<>();
    private boolean lazyCheck = false;
    private boolean autoDelete = true;

    @Data
    public static class Job {
        private String alias;
        private String cron;
        private Map<String, List<String>> parameters = new HashMap<>();
    }
}
