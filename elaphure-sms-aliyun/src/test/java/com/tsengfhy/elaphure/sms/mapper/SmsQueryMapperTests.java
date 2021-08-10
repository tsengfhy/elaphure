package com.tsengfhy.elaphure.sms.mapper;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.tsengfhy.elaphure.constants.DateFormat;
import com.tsengfhy.elaphure.sms.domain.SmsQuery;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SmsQueryMapperTests {

    @Test
    void testQuery() {
        SmsQueryMapper mapper = SmsQueryMapper.INSTANCE;
        SmsQuery query = new SmsQuery()
                .setId(RandomStringUtils.random(8, false, true))
                .setPhone(RandomStringUtils.random(1, false, true))
                .setFromDate(LocalDateTime.now())
                .setToDate(LocalDateTime.now().plusDays(1))
                .setPageNo(1)
                .setPageSize(1);
        QuerySendDetailsRequest request = mapper.to(query);
        Assertions.assertEquals(request.getBizId(), query.getId());
        Assertions.assertEquals(request.getPhoneNumber(), query.getPhone());
        Assertions.assertEquals(request.getSendDate(), query.getFromDate().format(DateTimeFormatter.ofPattern(DateFormat.PURE_DATE_FORMAT)));
        Assertions.assertEquals(request.getCurrentPage().intValue(), query.getPageNo());
        Assertions.assertEquals(request.getPageSize().intValue(), query.getPageSize());
    }
}
