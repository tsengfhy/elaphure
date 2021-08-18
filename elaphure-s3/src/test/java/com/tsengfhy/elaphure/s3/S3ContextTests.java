package com.tsengfhy.elaphure.s3;

import io.minio.MinioClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class S3ContextTests {

    @Autowired(required = false)
    private MinioClient s3Client;

    @Test
    void testAutoConfiguration() {
        Assertions.assertNotNull(s3Client);
    }
}
