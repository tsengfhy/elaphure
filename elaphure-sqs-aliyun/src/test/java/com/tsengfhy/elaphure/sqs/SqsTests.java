package com.tsengfhy.elaphure.sqs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@SpringBootTest
public class SqsTests {

    @Autowired(required = false)
    private JmsTemplate jmsTemplate;

    private static final String QUEUE = "tsengfhy";

    @Test
    void testAutoConfiguration() {
        Assertions.assertNotNull(jmsTemplate);
    }

    @Test
    @Disabled("Only for manual testing")
    void testSend() {
        jmsTemplate.convertAndSend(QUEUE, "test");
    }

    @Test
    @Disabled("Only for manual testing")
    void testReceive() throws JMSException {
        TextMessage textMessage = (TextMessage) jmsTemplate.receive(QUEUE);
        if (textMessage != null) {
            Assertions.assertEquals(textMessage.getText(), "test");
        }
    }

    static class Listener {

        @JmsListener(destination = QUEUE)
        public void onMessage(Message message) throws JMSException {
            System.out.println(((TextMessage) message).getText());
        }
    }
}
