package com.tsengfhy.entry.config;

import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class S3Configuration {

    @Bean
    @ConditionalOnExpression("#{'${elaphure.access-key}'.isBlank() || '${elaphure.secret-key}'.isBlank()}")
    MinioClient minioClient() {
        return null;
    }
}
