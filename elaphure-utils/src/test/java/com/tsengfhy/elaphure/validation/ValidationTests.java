package com.tsengfhy.elaphure.validation;

import com.tsengfhy.entry.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@SpringBootTest(classes = Application.class)
class ValidationTests {

    @Autowired
    private Validator validator;

    @Test
    void testCron() {
        Domain validDomain = new Domain().setCron("0/1 * * * * ?");
        Assertions.assertFalse(validator.validate(validDomain).stream().map(ConstraintViolation::getPropertyPath).anyMatch(path -> path.toString().equals("cron")));

        Domain inValidDomain = new Domain().setCron("");
        Assertions.assertTrue(validator.validate(inValidDomain).stream().map(ConstraintViolation::getPropertyPath).anyMatch(path -> path.toString().equals("cron")));
    }
}
