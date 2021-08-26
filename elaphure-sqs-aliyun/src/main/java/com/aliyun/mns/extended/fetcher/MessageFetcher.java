package com.aliyun.mns.extended.fetcher;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.extended.javamessaging.Constants;
import com.aliyun.mns.extended.javamessaging.MNSMessageConsumer;
import com.aliyun.mns.extended.javamessaging.MNSQueueWrapper;
import com.aliyun.mns.extended.javamessaging.acknowledge.Acknowledger;
import com.aliyun.mns.extended.javamessaging.message.MNSBytesMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSJsonableMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSJsonableProperty;
import com.aliyun.mns.extended.javamessaging.message.MNSMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSMessage.JMSMessagePropertyValue;
import com.aliyun.mns.extended.javamessaging.message.MNSObjectMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSTextMessage;
import com.aliyun.mns.extended.util.ExponentialBackoffStrategy;
import com.aliyun.mns.model.Message;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageFetcher
        implements Runnable, MessageFetcherManager {

    private static final Log LOG = LogFactory.getLog(MessageFetcher.class);

    private static final int POLLING_WAIT_SECONDS = 10;

    private MNSMessageConsumer consumer;
    private MNSQueueWrapper queue;
    private MessageListener listener = null;

    private Acknowledger acknowledger;
    private int ackMode;

    /**
     * Counter on how many messages are prefetched into internal messageQueue.
     */
    protected int messagesPrefetched = 0;

    /**
     * Controls the number of retry attempts to the MNS
     */
    protected int retriesAttempted = 0;

    /**
     * This backoff is on top of that to let the prefetch thread backoff after SDK completes re-tries with a max delay
     * of 2 seconds and 25ms delayInterval.
     */
    protected ExponentialBackoffStrategy backoffStrategy = new ExponentialBackoffStrategy(25, 25, 2000);

    /**
     * States of the prefetch thread
     */
    protected volatile boolean closed = false;
    protected volatile boolean running = false;

    private final Object stateLock = new Object();

    public MessageFetcher(MNSQueueWrapper queue, Acknowledger acknowledger, int ackMode) {
        this.queue = queue;
        this.acknowledger = acknowledger;
        this.ackMode = ackMode;
    }

    public void setMessageListener(MessageListener listener) {
        this.listener = listener;

        if (listener == null || isClosed()) {
            return;
        }

        synchronized (stateLock) {
            if (!running || isClosed()) {
                return;
            }

            notifyStateChange();
        }
    }

    public MessageListener getMessageListener() {
        return listener;
    }

    public void run() {
        while (true) {
            Message message = null;
            try {
                if (isClosed()) {
                    break;
                }

                synchronized (stateLock) {
                    waitForStart();
                    waitForListener();
                }

                if (!isClosed()) {
                    message = getMessage();
                }

                if (message != null) {
                    processReceivedMessage(message);
                }
            } catch (InterruptedException e) {
                break;
            } catch (Throwable e) {
                LOG.error("Unexpected exception when fetch messages:", e);
            }
        }
    }

    /**
     * Pop messages with long-poll wait time of 20 seconds with available prefetch batch size and potential re-tries.
     */
    protected Message getMessage() throws InterruptedException {
        Message message = null;
        try {
            message = queue.popMessage(POLLING_WAIT_SECONDS);
            if (message == null) {
                LOG.debug("messages null");
            }
            retriesAttempted = 0;
        } catch (Exception e) {
            LOG.warn("Encountered exception during receive in ConsumerPrefetch thread", e);
            try {
                sleep(backoffStrategy.delayBeforeNextRetry(retriesAttempted++));
            } catch (InterruptedException ex) {
                LOG.warn("Interrupted while retrying on receive", ex);
                throw ex;
            }
        }
        return message;
    }

    protected javax.jms.Message convertToJMSMessage(Message message) throws JMSException {
        String messageBody = message.getMessageBodyAsString();

        MNSJsonableMessage jsonableMessage = JSON.parseObject(messageBody, MNSJsonableMessage.class);

        List<MNSJsonableProperty> properties = jsonableMessage.getProperties();

        Map<String, JMSMessagePropertyValue> jmsProperties = new HashMap<String, JMSMessagePropertyValue>();
        String messageType = MNSMessage.TEXT_MESSAGE_TYPE; //TODO default??
        for (MNSJsonableProperty property : properties) {
            if (MNSMessage.JMS_MNS_MESSAGE_TYPE.equals(property.getPropertyName())) {
                messageType = property.getPropertyValue();
            } else {
                JMSMessagePropertyValue jmsMessagePropertyValue = new JMSMessagePropertyValue(
                        property.getPropertyValue(), property.getPropertyType());
                jmsProperties.put(property.getPropertyName(), jmsMessagePropertyValue);
            }
        }

        javax.jms.Message jmsMessage = null;
        if (MNSMessage.BYTE_MESSAGE_TYPE.equals(messageType)) {
            try {
                jmsMessage = new MNSBytesMessage(jsonableMessage.getMessageBody(), jmsProperties,
                        acknowledger, queue.getQueueURL(), message.getReceiptHandle());
            } catch (JMSException e) {
                LOG.warn("MessageReceiptHandle - " + message.getReceiptHandle() +
                        "cannot be serialized to BytesMessage", e);
                throw e;
            }
        } else if (MNSMessage.OBJECT_MESSAGE_TYPE.equals(messageType)) {
            jmsMessage = new MNSObjectMessage(jsonableMessage.getMessageBody(), jmsProperties,
                    acknowledger, queue.getQueueURL(), message.getReceiptHandle());
        } else if (MNSMessage.TEXT_MESSAGE_TYPE.equals(messageType)) {
            jmsMessage = new MNSTextMessage(jsonableMessage.getMessageBody(), jmsProperties,
                    acknowledger, queue.getQueueURL(), message.getReceiptHandle());
        } else {
            throw new JMSException("Not a supported JMS message type");
        }

//        jmsMessage.setJMSDestination(destination) TODO
        return jmsMessage;
    }

    /**
     * Pushes messages to  callback scheduler for asynchronous message delivery
     *
     * @throws JMSException
     */
    protected void processReceivedMessage(Message message) throws JMSException {
        if (message == null) {
            return;
        }

        MessageListener listener2 = this.listener;
        if (listener2 != null) {
            try {
                javax.jms.Message jmsMessage = convertToJMSMessage(message);
                boolean callbackFailed = false;
                try {
                    listener2.onMessage(jmsMessage);
                } catch (Throwable ex) {
                    LOG.warn("Exception thrown from onMessage callback for message", ex);
                    callbackFailed = true;
                } finally {
                    if (!callbackFailed) {
                        MNSMessage mnsMessage = (MNSMessage) jmsMessage;
                        if (this.ackMode == Session.AUTO_ACKNOWLEDGE) {
                            mnsMessage.acknowledge();
                        }
                    }
                }
                synchronized (stateLock) {
                    notifyStateChange();
                }
            } catch (JMSException e) {
                //TODO can drop directly??
                // queue.deleteMessage(message.getReceiptHandle());
                LOG.warn("processReceivedMessages Exception: " + message.getMessageId() + " " + e.toString());
            }
        } else {
            try {
                this.queue.changeMessageVisibilityTimeout(message.getReceiptHandle(), 1);
            } catch (Exception e) {
                LOG.warn("changeMessageVisibilityTimeout fail: " + message.getReceiptHandle() + " " + e.toString());
            }
        }
    }

    public static class MessageManager {

        private final MessageFetcherManager prefetchManager;

        private final javax.jms.Message message;

        public MessageManager(MessageFetcherManager prefetchManager, javax.jms.Message message) {
            this.prefetchManager = prefetchManager;
            this.message = message;
        }

        public MessageFetcherManager getPrefetchManager() {
            return prefetchManager;
        }

        public javax.jms.Message getMessage() {
            return message;
        }
    }

    protected void waitForStart() throws InterruptedException {
        synchronized (stateLock) {
            while (!running && !isClosed()) {
                try {
                    LOG.info("wait for start");
                    stateLock.wait();
                    LOG.info("wakeup try to check listener");
                } catch (InterruptedException e) {
                    LOG.warn("Interrupted while waiting on consumer start", e);
                    throw e;
                }
            }
        }
    }

    protected void waitForListener() throws InterruptedException {
        synchronized (stateLock) {
            while (listener == null && !isClosed()) {
                try {
                    LOG.info("wait for listener");
                    stateLock.wait();
                    LOG.info("wakeup try to fetch");
                } catch (InterruptedException e) {
                    LOG.warn("Interrupted while waiting on listener", e);
                    throw e;
                }
            }
        }
    }

    protected void notifyStateChange() {
        synchronized (stateLock) {
            stateLock.notifyAll();
        }
    }

    public void start() {
        if (isClosed() || running) {
            return;
        }
        synchronized (stateLock) {
            running = true;
            notifyStateChange();
        }
    }

    public void stop() {
        if (isClosed() || !running) {
            return;
        }
        synchronized (stateLock) {
            running = false;
            notifyStateChange();
        }
    }

    public void close() {
        if (isClosed()) {
            return;
        }
        synchronized (stateLock) {
            closed = true;
            notifyStateChange();
            listener = null;
        }
    }

    protected boolean isClosed() {
        return closed;
    }

    public void messageDispatched() {
        synchronized (stateLock) {
            // messagesPrefetched--;
            //if (messagesPrefetched < numberOfMessagesToPrefetch) {
            notifyStateChange();
            //}
        }
    }

    public MNSMessageConsumer getConsumer() {
        return this.consumer;
    }

    public void setMessageConsumer(MNSMessageConsumer consumer) {
        this.consumer = consumer;
    }

    /**
     * Sleeps for the configured time.
     */
    protected void sleep(long sleepTimeMillis) throws InterruptedException {
        try {
            Thread.sleep(sleepTimeMillis);
        } catch (InterruptedException e) {
            throw e;
        }
    }

    public MNSQueueWrapper getQueue() {
        return queue;
    }

    public javax.jms.Message receive() throws Exception {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    public javax.jms.Message receive(long timeout) throws Exception {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }
}
