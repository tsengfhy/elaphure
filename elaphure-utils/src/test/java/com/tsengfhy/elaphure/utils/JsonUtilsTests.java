package com.tsengfhy.elaphure.utils;

import com.tsengfhy.entry.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;

@SpringBootTest(classes = Application.class)
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
        Optional.of(new Date()).ifPresent(date -> {
            Assertions.assertEquals(JsonUtils.fromJson(JsonUtils.toJson(date), Date.class), date);
        });
        Optional.of(LocalDate.now()).ifPresent(date -> {
            Assertions.assertEquals(JsonUtils.fromJson(JsonUtils.toJson(date), LocalDate.class), date);
        });
        Optional.of(LocalTime.now()).ifPresent(date -> {
            Assertions.assertEquals(JsonUtils.fromJson(JsonUtils.toJson(date), LocalTime.class), date);
        });
        Optional.of(LocalDateTime.now()).ifPresent(date -> {
            Assertions.assertEquals(JsonUtils.fromJson(JsonUtils.toJson(date), LocalDateTime.class), date);
        });
    }
}



