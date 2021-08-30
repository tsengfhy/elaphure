package com.tsengfhy.elaphure.sms;

import com.tsengfhy.elaphure.sms.domain.SmsMessage;
import com.tsengfhy.elaphure.sms.domain.SmsQuery;
import com.tsengfhy.elaphure.sms.exception.SmsException;

import java.util.List;

public interface SmsTemplate {

    void send(SmsMessage message) throws SmsException;

    void sendBatch(List<SmsMessage> messages) throws SmsException;

    List<SmsMessage> query(SmsQuery query) throws SmsException;

    SmsMessage receiveReply(String destinationName) throws SmsException;
}
