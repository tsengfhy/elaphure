package com.tsengfhy.elaphure.sms.utils;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public final class AliyunSmsUtils {

    private static final Pattern SIGN_PATTERN = Pattern.compile("【(\\w+)】");

    public static String parseSign(String origin) {
        return Optional.of(SIGN_PATTERN.matcher(origin))
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .map(String::trim)
                .orElse(origin);
    }
}
