package com.tsengfhy.elaphure.env;

import com.tsengfhy.elaphure.constant.Context;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = Context.PREFIX + ".s3")
public class S3Properties {

    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;
}
