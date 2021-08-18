package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.env.ContextProperties;
import com.tsengfhy.elaphure.env.S3Properties;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({MinioClient.class})
@EnableConfigurationProperties({S3Properties.class})
public class S3Config {

    @Bean
    @ConditionalOnMissingBean
    public MinioClient s3Client(S3Properties properties, ContextProperties contextProperties) {
        MinioClient.Builder builder = MinioClient.builder();
        builder.endpoint(properties.getEndpoint());
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(Optional.ofNullable(properties.getRegion()).orElseGet(() -> parseRegion(properties.getEndpoint()))).to(builder::region);
        final String accessKey = Optional.ofNullable(properties.getAccessKey()).orElseGet(contextProperties::getAccessKey);
        final String secretKey = Optional.ofNullable(properties.getSecretKey()).orElseGet(contextProperties::getSecretKey);
        builder.credentials(accessKey, secretKey);
        return builder.build();
    }

    private String parseRegion(String endpoint) {
        return Optional.ofNullable(endpoint)
                .filter(value -> value.endsWith("aliyuncs.com"))
                .map(value -> value.split("\\.")[0])
                .orElse(null);
    }
}
