package com.tsengfhy.elaphure.sms.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AliyunSmsUtilsTests {

    @Test
    void testParseSign() {
        final String pureSign = "Tsengfhy";
        final String wrappedSign = String.format(" 【%s】 ", pureSign);
        Assertions.assertEquals(AliyunSmsUtils.parseSign(wrappedSign), pureSign);
        Assertions.assertEquals(AliyunSmsUtils.parseSign(pureSign), pureSign);
    }
}
