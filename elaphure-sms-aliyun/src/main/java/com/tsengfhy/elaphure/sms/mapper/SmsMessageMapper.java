package com.tsengfhy.elaphure.sms.mapper;

import com.aliyuncs.dysmsapi.model.v20170525.*;
import com.tsengfhy.elaphure.constants.DateFormat;
import com.tsengfhy.elaphure.mapper.BaseMapper;
import com.tsengfhy.elaphure.sms.constants.SmsStatus;
import com.tsengfhy.elaphure.sms.domain.SmsMessage;
import com.tsengfhy.elaphure.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SmsMessageMapper extends BaseMapper<SmsMessage, SendSmsRequest> {

    SmsMessageMapper INSTANCE = Mappers.getMapper(SmsMessageMapper.class);

    @Mapping(source = "extId", target = "outId")
    @Mapping(source = "phone", target = "phoneNumbers")
    @Mapping(source = "sign", target = "signName")
    @Mapping(source = "template", target = "templateCode")
    @Mapping(source = "params", target = "templateParam", qualifiedByName = "toString")
    SendSmsRequest to(SmsMessage message);

    default SmsMessage from(SendSmsRequest request) {
        throw new UnsupportedOperationException();
    }

    default SendBatchSmsRequest toBatch(List<SmsMessage> messages) {
        if (messages == null) {
            return null;
        } else {
            SendBatchSmsRequest sendBatchSmsRequest = new SendBatchSmsRequest();
            sendBatchSmsRequest.setPhoneNumberJson(toJson(messages.stream().map(SmsMessage::getPhone)));
            sendBatchSmsRequest.setSignNameJson(toJson(messages.stream().map(SmsMessage::getSign)));
            sendBatchSmsRequest.setTemplateCode(messages.stream().map(SmsMessage::getTemplate).filter(StringUtils::isNotBlank).findFirst().orElse(null));
            sendBatchSmsRequest.setTemplateParamJson(toJson(messages.stream().map(SmsMessage::getParams)));
            return sendBatchSmsRequest;
        }
    }

    default <T> String toJson(Stream<T> stream) {
        return JsonUtils.toJson(stream.collect(Collectors.toList()));
    }

    @Mapping(source = "response.bizId", target = "id")
    @Mapping(source = "response.code", target = "status", qualifiedByName = "codeToStatus")
    @Mapping(source = "response.message", target = "message")
    @Mapping(expression = "java(LocalDateTime.now())", target = "sendDate")
    void update(SendSmsResponse response, @MappingTarget SmsMessage message);

    @Mapping(source = "batchResponse.bizId", target = "id")
    @Mapping(source = "batchResponse.code", target = "status", qualifiedByName = "codeToStatus")
    @Mapping(expression = "java(LocalDateTime.now())", target = "sendDate")
    void update(SendBatchSmsResponse batchResponse, @MappingTarget SmsMessage message);

    default void update(SendBatchSmsResponse batchResponse, List<SmsMessage> messages) {
        if (messages != null) {
            messages.forEach(message -> update(batchResponse, message));
        }
    }

    @Named("codeToStatus")
    default SmsStatus codeToStatus(String code) {
        if ("OK".equals(code)) {
            return SmsStatus.SUCCESS;
        }
        return SmsStatus.FAIL;
    }

    @Mapping(source = "outId", target = "extId")
    @Mapping(source = "phoneNum", target = "phone")
    @Mapping(source = "templateCode", target = "template")
    @Mapping(expression = "java(com.tsengfhy.elaphure.sms.utils.AliyunSmsUtils.parseSign(response.getContent()))", target = "sign")
    @Mapping(source = "sendStatus", target = "status", qualifiedByName = "toStatus")
    @Mapping(source = "errCode", target = "message")
    @Mapping(source = "sendDate", target = "sendDate", dateFormat = DateFormat.DATETIME_FORMAT)
    SmsMessage fromQuery(QuerySendDetailsResponse.SmsSendDetailDTO response);

    List<SmsMessage> fromQuery(List<QuerySendDetailsResponse.SmsSendDetailDTO> responses);

    @Named("toStatus")
    default SmsStatus toStatus(Long status) {
        if (status == 3) {
            return SmsStatus.SUCCESS;
        } else if (status == 2) {
            return SmsStatus.FAIL;
        }
        return null;
    }

    @Mapping(expression = "java(java.time.LocalDateTime.parse(map.get(\"send_time\"), DateTimeFormatter.ofPattern(com.tsengfhy.elaphure.constants.DateFormat.DATETIME_FORMAT)))", target = "sendDate")
    @Mapping(expression = "java(com.tsengfhy.elaphure.sms.utils.AliyunSmsUtils.parseSign(map.get(\"sign_name\")))", target = "sign")
    @Mapping(expression = "java(map.get(\"phone_number\"))", target = "phone")
    @Mapping(expression = "java(map.get(\"content\"))", target = "content")
    @Mapping(constant = "UP", target = "type")
    SmsMessage fromReply(Map<String, String> map);
}
