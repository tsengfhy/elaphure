package com.tsengfhy.elaphure.sms.mapper;

import com.aliyuncs.dysmsapi.model.v20170525.*;
import com.tsengfhy.elaphure.constants.DateFormat;
import com.tsengfhy.elaphure.sms.AliyunSmsTemplate;
import com.tsengfhy.elaphure.sms.constants.SmsStatus;
import com.tsengfhy.elaphure.sms.domain.SmsMessage;
import com.tsengfhy.elaphure.utils.JsonUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsMessageMapperTests {

    private static final SmsMessageMapper MAPPER = SmsMessageMapper.INSTANCE;

    private static final String ID = RandomStringUtils.random(11, false, true);
    private static final String EXT_ID = RandomStringUtils.random(11, false, true);
    private static final String PHONE = RandomStringUtils.random(11, false, true);
    private static final String SIGN = RandomStringUtils.random(7, true, false);
    private static final String TEMPLATE = RandomStringUtils.random(11);
    private static final String MESSAGE = RandomStringUtils.random(11);
    private static final LocalDateTime SEND_DATE = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.DATETIME_FORMAT)), DateTimeFormatter.ofPattern(DateFormat.DATETIME_FORMAT));

    @Test
    void testSend() {
        SmsMessage message = new SmsMessage()
                .setExtId(EXT_ID)
                .setPhone(PHONE)
                .setSign(SIGN)
                .setTemplate(TEMPLATE)
                .addParam("code", RandomStringUtils.random(6));
        SendSmsRequest request = MAPPER.to(message);
        Assertions.assertEquals(request.getOutId(), message.getExtId());
        Assertions.assertEquals(request.getPhoneNumbers(), message.getPhone());
        Assertions.assertEquals(request.getSignName(), message.getSign());
        Assertions.assertEquals(JsonUtils.fromJson(request.getTemplateParam(), HashMap.class).get("code"), message.getParams().get("code"));

        SendSmsResponse response = new SendSmsResponse();
        response.setBizId(ID);
        response.setCode(AliyunSmsTemplate.SUCCESS_CODE);
        response.setMessage(MESSAGE);
        MAPPER.update(response, message);
        Assertions.assertEquals(message.getId(), response.getBizId());
        Assertions.assertEquals(message.getStatus(), SmsStatus.SUCCESS);
        Assertions.assertEquals(message.getMessage(), response.getMessage());
        Assertions.assertNotNull(message.getSendDate());
    }

    @Test
    void testSendBatch() {
        List<SmsMessage> list = new ArrayList<>();
        list.add(
                new SmsMessage()
                        .setPhone(RandomStringUtils.random(11, false, true))
                        .setSign(RandomStringUtils.random(7))
                        .setTemplate(TEMPLATE)
                        .addParam("code", RandomStringUtils.random(6))
        );
        list.add(
                new SmsMessage()
                        .setPhone(RandomStringUtils.random(11, false, true))
                        .setSign(RandomStringUtils.random(7))
                        .setTemplate(TEMPLATE)
                        .addParam("code", RandomStringUtils.random(6))
        );
        SendBatchSmsRequest batchRequest = MAPPER.toBatch(list);
        Assertions.assertTrue(JsonUtils.fromJson(batchRequest.getPhoneNumberJson(), ArrayList.class).contains(list.stream().map(SmsMessage::getPhone).findAny().get()));
        Assertions.assertTrue(JsonUtils.fromJson(batchRequest.getSignNameJson(), ArrayList.class).contains(list.stream().map(SmsMessage::getSign).findAny().get()));
        Assertions.assertEquals(batchRequest.getTemplateCode(), list.stream().map(SmsMessage::getTemplate).findAny().get());

        SendBatchSmsResponse batchResponse = new SendBatchSmsResponse();
        batchResponse.setBizId(ID);
        batchResponse.setCode(AliyunSmsTemplate.SUCCESS_CODE);
        batchResponse.setMessage(MESSAGE);
        MAPPER.update(batchResponse, list);
        Assertions.assertEquals(list.stream().map(SmsMessage::getId).findAny().get(), batchResponse.getBizId());
        Assertions.assertEquals(list.stream().map(SmsMessage::getStatus).findAny().get(), SmsStatus.SUCCESS);
        Assertions.assertEquals(list.stream().map(SmsMessage::getMessage).findAny().get(), batchResponse.getMessage());
        Assertions.assertTrue(list.stream().map(SmsMessage::getSendDate).findAny().isPresent());
    }

    @Test
    void testQuery() {
        QuerySendDetailsResponse.SmsSendDetailDTO response = new QuerySendDetailsResponse.SmsSendDetailDTO();
        response.setOutId(EXT_ID);
        response.setPhoneNum(PHONE);
        response.setTemplateCode(TEMPLATE);
        response.setContent(String.format(" 【%s】 test message", SIGN));
        response.setSendStatus(3L);
        response.setErrCode(MESSAGE);
        response.setSendDate(SEND_DATE.format(DateTimeFormatter.ofPattern(DateFormat.DATETIME_FORMAT)));
        SmsMessage message = MAPPER.fromQuery(response);
        Assertions.assertEquals(message.getExtId(), response.getOutId());
        Assertions.assertEquals(message.getPhone(), response.getPhoneNum());
        Assertions.assertEquals(message.getTemplate(), response.getTemplateCode());
        Assertions.assertEquals(message.getSign(), SIGN);
        Assertions.assertEquals(message.getContent(), response.getContent());
        Assertions.assertEquals(message.getStatus(), SmsStatus.SUCCESS);
        Assertions.assertEquals(message.getMessage(), response.getErrCode());
        Assertions.assertEquals(message.getSendDate(), SEND_DATE);
    }

    @Test
    void testReply() {
        Map<String, String> map = new HashMap<>();
        map.put("send_time", SEND_DATE.format(DateTimeFormatter.ofPattern(DateFormat.DATETIME_FORMAT)));
        map.put("sign_name", String.format(" 【%s】 ", SIGN));
        map.put("phone_number", PHONE);
        map.put("content", RandomStringUtils.random(20));
        SmsMessage message = MAPPER.fromReply(map);
        Assertions.assertEquals(message.getSendDate(), SEND_DATE);
        Assertions.assertEquals(message.getSign(), SIGN);
        Assertions.assertEquals(message.getPhone(), map.get("phone_number"));
        Assertions.assertEquals(message.getContent(), map.get("content"));
    }
}
