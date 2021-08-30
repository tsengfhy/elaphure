package com.tsengfhy.elaphure.sms;

import com.tsengfhy.elaphure.constants.DateFormat;
import com.tsengfhy.elaphure.sms.annotation.SmsListener;
import com.tsengfhy.elaphure.sms.constants.SmsStatus;
import com.tsengfhy.elaphure.sms.domain.SmsMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
public class SmsTests {

    @Autowired(required = false)
    private SmsTemplate smsTemplate;

    @Test
    void testAutoConfiguration() {
        Assertions.assertNotNull(smsTemplate);
    }

    @Test
    @Disabled("Only for manual testing")
    void testSend() {
        SmsMessage message = new SmsMessage()
                .setPhone("18604286610")
                .setSign("Tsengfhy")
                .setTemplate("SMS_221726670")
                .addParam("function", "Send Message")
                .addParam("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.DATETIME_FORMAT)));
        smsTemplate.send(message);
        Assertions.assertEquals(message.getStatus(), SmsStatus.SUCCESS);
    }

    static class Listener {

        @SmsListener
        public boolean onMessage(SmsMessage message) {
            System.out.println(message.getContent());
            return true;
        }
    }
}
