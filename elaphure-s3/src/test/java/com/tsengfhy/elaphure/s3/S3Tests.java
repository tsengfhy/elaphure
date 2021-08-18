package com.tsengfhy.elaphure.s3;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled("Only for manual testing")
public class S3Tests {

    @Autowired(required = false)
    private MinioClient s3Client;

    private static final String BUCKET = "tsengfhy";

    private static final String PATH = "img";

    private static final String OBJECT = "avatar.jpg";

    @BeforeAll
    @SneakyThrows
    void setUp() {
        if (s3Client.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build())) {
            for (Result<Item> result : s3Client.listObjects(ListObjectsArgs.builder().bucket(BUCKET).recursive(true).build())) {
                s3Client.removeObject(RemoveObjectArgs.builder().bucket(BUCKET).object(result.get().objectName()).build());
            }
            s3Client.removeBucket(RemoveBucketArgs.builder().bucket(BUCKET).build());
        }
        s3Client.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
    }

    @Test
    @SneakyThrows
    void testBucket() {
        Assertions.assertTrue(s3Client.listBuckets().stream().map(Bucket::name).anyMatch(BUCKET::equals));
    }

    @Test
    @SneakyThrows
    void testObject() {
        final String key = PATH + "/" + OBJECT;
        Assertions.assertThrows(MinioException.class, () -> s3Client.statObject(StatObjectArgs.builder().bucket(BUCKET).object(key).build()));
        InputStream is = new ClassPathResource("/static/img/" + OBJECT).getInputStream();
        s3Client.putObject(PutObjectArgs.builder().bucket(BUCKET).object(key).stream(is, is.available(), -1).contentType("image/jpeg").build());
        final String url = s3Client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(BUCKET).method(Method.GET).object(key).expiry(1, TimeUnit.DAYS).build());
        System.out.println(url);
        Assertions.assertEquals(new OkHttpClient().newCall(new Request.Builder().get().url(url).build()).execute().code(), 200);
    }
}
