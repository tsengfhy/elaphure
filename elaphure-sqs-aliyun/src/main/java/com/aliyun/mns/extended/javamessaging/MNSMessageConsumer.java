package com.aliyun.mns.extended.javamessaging;

import com.aliyun.mns.extended.fetcher.MessageFetcher;
import com.aliyun.mns.extended.javamessaging.acknowledge.Acknowledger;
import com.aliyun.mns.extended.util.ThreadFactoryHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MNSMessageConsumer implements MessageConsumer, QueueReceiver {
    private static final Log LOG = LogFactory.getLog(MNSMessageConsumer.class);

    public static final int PREFETCH_EXECUTOR_GRACEFUL_SHUTDOWN_TIME = 32;

//    public static final int POLLING_WAIT_SECONDS_IN_RECEIVE = 20;

    private MNSQueueConnection parentConnection;

    private MNSQueueSession parentSession;

    private MNSQueueDestination destination;

    private MNSQueueWrapper mnsQueueWrapper;

    protected volatile boolean closed = false;

    private Acknowledger acknowledger;
    // private CallbackScheduler callbackScheduler;

    /**
     * Executor for prefetch thread.
     */
    private final ExecutorService prefetchExecutor;

    private final MessageFetcher messagePrefetcher;

    private final ThreadFactoryHelper prefetchThreadHelper;

    public MNSMessageConsumer(MNSQueueConnection connection,
                              MNSQueueSession session,
                              Acknowledger acknowledger,
                              MNSClientWrapper mnsClientWrapper, MNSQueueDestination destination,
                              ThreadFactoryHelper prefetchThreadHelper) throws JMSException {
        this.parentConnection = connection;
        this.parentSession = session;
        this.prefetchThreadHelper = prefetchThreadHelper;
        this.acknowledger = acknowledger;

        this.mnsQueueWrapper = mnsClientWrapper.generateMNSQueueWrapper(destination.getQueueName());
        messagePrefetcher = new MessageFetcher(this.mnsQueueWrapper, this.acknowledger, this.parentSession.getAcknowledgeMode());
        messagePrefetcher.setMessageConsumer(this);
        prefetchExecutor = Executors.newSingleThreadExecutor(this.prefetchThreadHelper);
        prefetchExecutor.execute(messagePrefetcher);
    }

    @Override
    public Queue getQueue() throws JMSException {
        return (Queue) destination;
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return this.messagePrefetcher.getMessageListener();
    }

    @Override
    public void setMessageListener(MessageListener listener)
            throws JMSException {
        this.messagePrefetcher.setMessageListener(listener);
    }

    @Override
    public void close() throws JMSException {
        if (closed) {
            return;
        }

        doClose();
    }

    void doClose() {
        if (closed) {
            return;
        }

        messagePrefetcher.close();
        parentSession.removeConsumer(this);

        try {
            if (!prefetchExecutor.isShutdown()) {
                LOG.info("Shutting down fetcher executor");
                /** Shut down executor. */
                prefetchExecutor.shutdown();
            }

            parentSession.waitForConsumerCallbackToComplete(this);

            if (!prefetchExecutor.awaitTermination(PREFETCH_EXECUTOR_GRACEFUL_SHUTDOWN_TIME, TimeUnit.SECONDS)) {

                LOG.warn("Can't terminate executor service perfetcher after "
                        + PREFETCH_EXECUTOR_GRACEFUL_SHUTDOWN_TIME
                        + " seconds, some running threads will be shutdown immediately");
                prefetchExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOG.error("Interrupted while closing the consumer.", e);
        }

        closed = true;
    }

    //TODO
    void recover() throws JMSException {
    }

    public boolean isClosed() {
        return closed;
    }

    public void startPrefetch() {
        this.messagePrefetcher.start();
    }

    public void stopPrefetch() {
        this.messagePrefetcher.stop();
    }

    @Override
    public String getMessageSelector() throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    @Override
    public Message receive() throws JMSException {
        Message message = null;
        try {
            message = messagePrefetcher.convertToJMSMessage(messagePrefetcher.getMessage());
            return message;
        } catch (InterruptedException e) {
            throw new JMSException(e.getMessage());
        } finally {
            if (message != null && this.parentSession.getAcknowledgeMode() == Session.AUTO_ACKNOWLEDGE) {
                message.acknowledge();
            }
        }
    }

    @Override
    public Message receive(long timeout) throws JMSException {
        Message message = null;
        try {
            message = messagePrefetcher.convertToJMSMessage(messagePrefetcher.getMessage((int) timeout / 1000));
            return message;
        } catch (InterruptedException e) {
            throw new JMSException(e.getMessage());
        } finally {
            if (message != null && this.parentSession.getAcknowledgeMode() == Session.AUTO_ACKNOWLEDGE) {
                message.acknowledge();
            }
        }
    }

    @Override
    public Message receiveNoWait() throws JMSException {
        Message message = null;
        try {
            message = messagePrefetcher.convertToJMSMessage(messagePrefetcher.getMessage(0));
            return message;
        } catch (InterruptedException e) {
            throw new JMSException(e.getMessage());
        } finally {
            if (message != null && this.parentSession.getAcknowledgeMode() == Session.AUTO_ACKNOWLEDGE) {
                message.acknowledge();
            }
        }
    }
}
