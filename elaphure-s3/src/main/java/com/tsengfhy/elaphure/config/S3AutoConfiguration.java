package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.env.ContextProperties;
import com.tsengfhy.elaphure.env.S3Properties;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@AutoConfiguration
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(S3Properties.class)
class S3AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    MinioClient minioClient(S3Properties s3Properties) {
        return MinioClient.builder()
                .region(s3Properties.getRegion())
                .endpoint(s3Properties.getEndpoint())
                .credentials(s3Properties.getAccessKey(), s3Properties.getSecretKey())
                .build();
    }
}
