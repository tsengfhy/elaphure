package com.tsengfhy.elaphure.s3;

import com.tsengfhy.entry.Application;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = Application.class)
@EnabledIf(expression = "#{!'${elaphure.access-key}'.isBlank() && !'${elaphure.secret-key}'.isBlank()}", loadContext = true)
class S3Tests {

    @Autowired
    private MinioClient minioClient;

    private static final String BUCKET = "global-tsengfhy-test";
    private static final String OBJECT = "avatar.jpg";

    @BeforeEach
    @SneakyThrows
    void setUp() {
        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build())) {
            clear();
        } else {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
        }
    }

    @AfterEach
    @SneakyThrows
    void tearDown() {
        clear();
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(BUCKET).build());
    }

    @SneakyThrows
    void clear() {
        for (Result<Item> result : minioClient.listObjects(ListObjectsArgs.builder().bucket(BUCKET).recursive(true).build())) {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(BUCKET).object(result.get().objectName()).build());
        }
    }

    @Test
    @SneakyThrows
    void testBucket() {
        Assertions.assertTrue(minioClient.listBuckets().stream().map(Bucket::name).anyMatch(BUCKET::equals));
    }

    @Test
    @SneakyThrows
    void testObject() {
        Assertions.assertThrows(MinioException.class, () -> minioClient.statObject(StatObjectArgs.builder().bucket(BUCKET).object(OBJECT).build()));

        try (InputStream is = new ClassPathResource("/static/img/" + OBJECT).getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder().bucket(BUCKET).object(OBJECT).stream(is, is.available(), -1).contentType("image/jpeg").build());
            final String presignedObjectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(BUCKET).method(Method.GET).object(OBJECT).expiry(1, TimeUnit.MINUTES).build());
            Assertions.assertEquals(200, new OkHttpClient().newCall(new Request.Builder().get().url(presignedObjectUrl).build()).execute().code());
        }
    }
}
