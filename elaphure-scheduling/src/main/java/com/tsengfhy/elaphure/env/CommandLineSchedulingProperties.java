package com.tsengfhy.elaphure.env;

import com.tsengfhy.elaphure.constants.Context;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = Context.PREFIX + ".scheduling.command-line")
public class CommandLineSchedulingProperties {

    private boolean enabled = true;
}
