package com.tsengfhy.elaphure.env;

import com.tsengfhy.elaphure.constants.Context;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = Context.PREFIX + ".sqs")
public class SqsProperties {

    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;
}
