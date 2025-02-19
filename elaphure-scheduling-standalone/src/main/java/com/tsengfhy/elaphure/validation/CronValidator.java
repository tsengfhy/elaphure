package com.tsengfhy.elaphure.validation;

import com.tsengfhy.elaphure.validation.annotation.Cron;
import org.springframework.scheduling.support.CronExpression;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;

public class CronValidator implements ConstraintValidator<Cron, String> {

    private boolean required;

    @Override
    public void initialize(Cron constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return Optional.ofNullable(value).map(CronExpression::isValidExpression).orElse(!required);
    }
}
