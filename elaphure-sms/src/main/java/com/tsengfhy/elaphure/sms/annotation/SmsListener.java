package com.tsengfhy.elaphure.sms.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(SmsListeners.class)
public @interface SmsListener {

    String id() default "";

    String containerFactory() default "";
}
