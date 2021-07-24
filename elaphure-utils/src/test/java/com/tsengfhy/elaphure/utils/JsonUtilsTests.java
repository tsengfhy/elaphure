package com.tsengfhy.elaphure.utils;

import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Date;

@SpringBootTest
class JsonUtilsTests {

    @Test
    void testToJson() {
        Assertions.assertNull(JsonUtils.toJson(null));
        Assertions.assertNotNull(JsonUtils.toJson(new Object()));
    }

    @Test
    void testFromJson() {
        Assertions.assertNull(JsonUtils.fromJson(null, Object.class));
        Assertions.assertNull(JsonUtils.fromJson("  ", Object.class));
    }

    @Test
    void testMatch() {
        TestDTO dto = new TestDTO().setValue("test").setDate(OffsetDateTime.now());
        Assertions.assertEquals(dto.getValue(), JsonUtils.fromJson(JsonUtils.toJson(dto), TestDTO.class).getValue());
    }

    @Data
    @Accessors(chain = true)
    static class TestDTO {
        private String value;
        private OffsetDateTime date;
    }

    @Test
    void testDate() {
        JsonUtils.fromJson(JsonUtils.toJson(new Date()), Date.class);
        JsonUtils.fromJson(JsonUtils.toJson(LocalDate.now()), LocalDate.class);
        JsonUtils.fromJson(JsonUtils.toJson(LocalTime.now()), LocalTime.class);
        JsonUtils.fromJson(JsonUtils.toJson(LocalDateTime.now()), LocalDateTime.class);
        JsonUtils.fromJson(JsonUtils.toJson(OffsetDateTime.now()), OffsetDateTime.class);
    }
}



