package com.aliyun.mns.extended.javamessaging;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.JMSException;
import java.util.List;

public class MNSQueueWrapper {
    private static final Log LOG = LogFactory.getLog(MNSQueueWrapper.class);

    private CloudQueue queue = null;

    public MNSQueueWrapper(CloudQueue queue) {
        this.queue = queue;
    }

    public Message sendMessage(Message message) throws JMSException {
        try {
            return this.queue.putMessage(message);
            //TODO handle all exceptions ??
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private JMSException handleException(Exception e) {
        JMSException jmsException;
        if (e instanceof ClientException) {
            ClientException exception = (ClientException) e;
            LOG.error(exception);
            jmsException = new JMSException(exception.getMessage(), exception.getErrorCode());
        } else if (e instanceof ServiceException) {
            ServiceException exception = (ServiceException) e;
            LOG.error(exception);
            jmsException = new JMSException(exception.getMessage(), exception.getErrorCode());
        } else {
            LOG.error(e);
            jmsException = new JMSException(e.getMessage());
        }
        jmsException.initCause(e);
        return jmsException;
    }

    public void changeMessageVisibilityTimeout(String receiptHandle,
                                               int visibilityTimeout) throws JMSException {
        try {
            this.queue.changeMessageVisibilityTimeout(receiptHandle, visibilityTimeout);
        } catch (Exception e) {
            if (e instanceof ServiceException
                    && queue.isMessageNotExist((ServiceException) e)) {
                LOG.warn("MessageNotExist for gaven ReceiptHandle: " + receiptHandle);
            } else {
                throw handleException(e);
            }
        }
    }

    public void deleteMessage(String receiptHandle) throws JMSException {
        try {
            this.queue.deleteMessage(receiptHandle);
        } catch (Exception e) {
            if (e instanceof ServiceException
                    && queue.isMessageNotExist((ServiceException) e)) {
                LOG.warn("MessageNotExist for gaven ReceiptHandle: " + receiptHandle);
            } else {
                throw handleException(e);
            }
        }
    }

    public List<Message> batchPopMessages(int prefetchBatchSize,
                                          int pollingWaitSeconds) throws JMSException {
        try {
            return this.queue.batchPopMessage(prefetchBatchSize, pollingWaitSeconds);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    public Message popMessage(int pollingWaitSeconds) throws JMSException {
        try {
            return this.queue.popMessage(pollingWaitSeconds);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    public String getQueueURL() {
        return queue.getQueueURL();
    }
}
