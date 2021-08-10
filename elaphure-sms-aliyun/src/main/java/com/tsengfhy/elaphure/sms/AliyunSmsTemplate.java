package com.tsengfhy.elaphure.sms;

import com.alibaba.cloud.spring.boot.sms.ISmsService;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendBatchSmsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.tsengfhy.elaphure.sms.domain.SmsMessage;
import com.tsengfhy.elaphure.sms.domain.SmsQuery;
import com.tsengfhy.elaphure.sms.exception.SmsClientException;
import com.tsengfhy.elaphure.sms.exception.SmsException;
import com.tsengfhy.elaphure.sms.exception.SmsServerException;
import com.tsengfhy.elaphure.sms.mapper.SmsMessageMapper;
import com.tsengfhy.elaphure.sms.mapper.SmsQueryMapper;
import com.tsengfhy.elaphure.utils.JsonUtils;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
public class AliyunSmsTemplate implements SmsTemplate, InitializingBean {

    private final ISmsService smsService;

    public static final String SUCCESS_CODE = "OK";

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(smsService, "SmsService must not be null");
    }

    @Override
    public void send(SmsMessage message) throws SmsException {
        try {
            SendSmsResponse response = smsService.sendSmsRequest(SmsMessageMapper.INSTANCE.to(message));
            SmsMessageMapper.INSTANCE.update(response, message);
        } catch (ServerException e) {
            throw new SmsServerException(e);
        } catch (ClientException e) {
            throw new SmsClientException(e);
        }
    }

    @Override
    public void sendBatch(List<SmsMessage> messages) throws SmsException {
        try {
            if (messages != null && messages.stream().map(SmsMessage::getTemplate).collect(Collectors.toSet()).size() > 1) {
                throw new SmsClientException("UnmatchedTemplateCode : TemplateCode is mandatory to the same");
            }
            SendBatchSmsResponse batchResponse = smsService.sendSmsBatchRequest(SmsMessageMapper.INSTANCE.toBatch(messages));
            SmsMessageMapper.INSTANCE.update(batchResponse, messages);
        } catch (ServerException e) {
            throw new SmsServerException(e);
        } catch (ClientException e) {
            throw new SmsClientException(e);
        }
    }

    @Override
    public List<SmsMessage> query(SmsQuery query) throws SmsException {
        try {
            if (query.getFromDate() != null && query.getToDate() != null && !query.getFromDate().equals(query.getToDate())) {
                throw new SmsClientException("UnmatchedSendDate: For Aliyun SMS, FromDate and ToDate is mandatory to the same");
            }
            QuerySendDetailsResponse queryResponse = smsService.querySendDetails(SmsQueryMapper.INSTANCE.to(query));
            if (!StringUtils.equals(queryResponse.getCode(), SUCCESS_CODE)) {
                throw new SmsServerException(queryResponse.getMessage());
            }
            return SmsMessageMapper.INSTANCE.fromQuery(queryResponse.getSmsSendDetailDTOs());
        } catch (ClientException e) {
            throw new SmsClientException(e);
        }
    }

    @Override
    public void setReplyListener(Function<SmsMessage, Boolean> function) throws SmsException {
        smsService.startSmsUpMessageListener(message -> function.apply(SmsMessageMapper.INSTANCE.fromReply(JsonUtils.fromJson(message.getMessageBody(), HashMap.class))));
    }
}
