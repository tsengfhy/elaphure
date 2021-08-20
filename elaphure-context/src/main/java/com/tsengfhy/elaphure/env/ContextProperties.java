package com.tsengfhy.elaphure.env;

import com.tsengfhy.elaphure.constants.Context;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = Context.PREFIX)
public class ContextProperties {

    private String accessKey;
    private String secretKey;
}
