package com.tsengfhy.elaphure.env;

import com.tsengfhy.elaphure.constant.Context;
import com.tsengfhy.elaphure.validation.annotation.Cron;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Validated
@ConfigurationProperties(prefix = Context.PREFIX + ".scheduling.standalone")
public class StandaloneSchedulingProperties {

    private boolean enabled = true;
    private boolean lazyCheck = false;
    private boolean autoDelete = true;
    @NestedConfigurationProperty
    private Map<String, Map<String, Job>> jobs = new HashMap<>();

    @Data
    public static class Job {
        private String alias;
        @Cron
        private String cron;
        private Map<String, List<String>> parameters = new HashMap<>();
    }
}
