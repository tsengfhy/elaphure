package com.tsengfhy.elaphure.env;

import com.tsengfhy.elaphure.constant.Context;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = Context.PREFIX + ".s3")
public class S3Properties {

    private String endpoint;
    private String region;
    @Value("${" + Context.PREFIX + ".s3.access-key:${" + Context.PREFIX + ".access-key:}}")
    private String accessKey;
    @Value("${" + Context.PREFIX + ".s3.secret-key:${" + Context.PREFIX + ".secret-key:}}")
    private String secretKey;
}
