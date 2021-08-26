package com.aliyun.mns.extended.javamessaging;

import com.aliyun.mns.extended.javamessaging.acknowledge.AcknowledgeMode;
import com.aliyun.mns.extended.javamessaging.acknowledge.Acknowledger;
import com.aliyun.mns.extended.javamessaging.message.MNSBytesMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSObjectMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSTextMessage;
import com.aliyun.mns.extended.util.ThreadFactoryHelper;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MNSQueueSession implements Session, QueueSession {
    private final Log LOG = LogFactory.getLog(MNSQueueSession.class);

    public static final int MANUAL_ACKNOWLEDGE = 100;

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean closing = new AtomicBoolean(false);

    private final MNSClientWrapper mnsClientWrapper;
    private final MNSQueueConnection parentConnection;

    /**
     * AcknowledgeMode of this Session.
     */
    private final AcknowledgeMode acknowledgeMode;

    private final Acknowledger acknowledger;

    /**
     * Set of MessageProducer under this Session
     */
    private final Set<MNSMessageProducer> messageProducers;

    /**
     * Set of MessageConsumer under this Session
     */
    private final Set<MNSMessageConsumer> messageConsumers;

    /**
     * Used to create callback scheduler threads
     */
    static final ThreadFactoryHelper CALLBACK_SCHEDULER_THREAD_FACTORY = new ThreadFactoryHelper(
            "CallbackSchedulers", true);

    /**
     * Used to create consumer fetcher threads
     */
    static final ThreadFactoryHelper CONSUMER_PREFETCH_THREAD_FACTORY = new ThreadFactoryHelper(
            "Prefetchers", true);

    /**
     * Executor service for running callbacks.
     */
    // private final ExecutorService callbackExecutor;

    // private final CallbackScheduler callbackScheduler;

    /**
     * Used to determine if the caller thread is the session callback thread. Guarded by stateLock
     */
    private Thread activeCallbackSessionThread;

    /**
     * Used to determine the active consumer, whose is dispatching the message on the callback. Guarded by stateLock
     */
    private MNSMessageConsumer activeConsumerInCallback = null;

    private final Object stateLock = new Object();

    public MNSQueueSession(MNSQueueConnection parentConnection, AcknowledgeMode acknowledgeMode) throws JMSException {
        this(parentConnection, acknowledgeMode,
                Collections.newSetFromMap(new ConcurrentHashMap<MNSMessageProducer, Boolean>()),
                Collections.newSetFromMap(new ConcurrentHashMap<MNSMessageConsumer, Boolean>()));
    }

    public MNSQueueSession(MNSQueueConnection parentConnection, AcknowledgeMode acknowledgeMode,
                           Set<MNSMessageProducer> producers, Set<MNSMessageConsumer> consumers) throws JMSException {
        this.parentConnection = parentConnection;
        this.mnsClientWrapper = parentConnection.getMNSClientWrapper();

        this.acknowledgeMode = acknowledgeMode;
        this.acknowledger = this.acknowledgeMode.createAcknowledger(this.mnsClientWrapper, this);

        this.messageProducers = producers;
        this.messageConsumers = consumers;

        //WARNNING: must be single thread
        // this.callbackExecutor = Executors.newSingleThreadExecutor(CALLBACK_SCHEDULER_THREAD_FACTORY);
        // this.callbackScheduler = new CallbackScheduler(this, this.acknowledgeMode);

        // this.callbackExecutor.execute(this.callbackScheduler);
    }

    @Override
    public boolean getTransacted() throws JMSException {
        return false;
    }

    @Override
    public int getAcknowledgeMode() throws JMSException {
        return this.acknowledgeMode.getOriginalAcknowledgeMode();
    }

    public void start() throws IllegalStateException {
        checkClosed();

        synchronized (stateLock) {
            checkClosing();
            running.set(true);
            for (MNSMessageConsumer consumer : messageConsumers) {
                consumer.startPrefetch();
            }

            stateLock.notifyAll();
        }
    }

    public void stop() throws IllegalStateException {
        checkClosed();

        synchronized (stateLock) {
            checkClosing();
            running.set(false);

            for (MNSMessageConsumer consumer : messageConsumers) {
                consumer.stopPrefetch();
            }
            waitForCallbackComplete();

            stateLock.notifyAll();
        }
    }

    /**
     * Check if session is closed.
     */
    public void checkClosed() throws IllegalStateException {
        if (closed.get()) {
            throw new IllegalStateException("Session is closed");
        }
    }

    public void checkClosing() throws IllegalStateException {
        if (closing.get()) {
            throw new IllegalStateException("Session is closing");
        }
    }

    @Override
    public synchronized void close() throws JMSException {
        if (!closed.get()) {

            closing.set(true);

            /**
             * A MessageListener must not attempt to close its own Session as
             * this would lead to deadlock
             */
            if (isActiveCallbackSessionThread()) {
                throw new IllegalStateException(
                        "MessageListener must not attempt to close its own Session to prevent potential deadlock issues");
            }

            try {
                parentConnection.removeSession(this);
                //stop runner threads

                for (MNSMessageProducer producer : messageProducers) {
                    producer.close();
                }

                for (MNSMessageConsumer consumer : messageConsumers) {
                    consumer.close();
                    //back messages to queue
                    consumer.recover();
                }
                /* if (callbackExecutor != null) {
                    LOG.info("shutdown callbackExecutor");
                    callbackExecutor.shutdown();
                    waitForCallbackComplete();
                    callbackScheduler.close();

                    if (!callbackExecutor.awaitTermination(10, TimeUnit.SECONDS)) {

                        LOG.warn("Can't terminate executor service after 10 seconds,"
                                + " some running threads will be shutdown immediately");
                        callbackExecutor.shutdownNow();
                    }
                }*/
            }
            /* catch (InterruptedException e) {
                LOG.warn("Interrupted while closing session.", e);
            }*/ finally {
                closed.set(true);
            }
        }
    }

    /**
     * back all messages in consumers associated this session
     */
    @Override
    public void recover() throws JMSException {
        //TODO
        for (MNSMessageConsumer consumer : messageConsumers) {
            consumer.recover();
        }
    }

    public void startingCallback(MNSMessageConsumer consumer) throws InterruptedException, Exception {
        if (closed.get()) {
            return;
        }

        synchronized (stateLock) {
            if (activeConsumerInCallback != null) {
                throw new IllegalStateException("Callback already in progress");
            }
            assert activeCallbackSessionThread == null;

            while (!running.get() && !closing.get()) {
                try {
                    stateLock.wait();
                } catch (InterruptedException e) {
                    LOG.warn("Interrupted while waiting on session start. Continue to wait...", e);
                }
            }
            checkClosing();

            activeConsumerInCallback = consumer;
            activeCallbackSessionThread = Thread.currentThread();
        }
    }

    public void finishedCallback() throws Exception {
        synchronized (stateLock) {
            if (activeConsumerInCallback == null) {
                throw new IllegalStateException("Callback not in progress");
            }
            activeConsumerInCallback = null;
            activeCallbackSessionThread = null;
            stateLock.notifyAll();
        }
    }

    /**
     * @return True if the current thread is the callback thread
     */
    public boolean isActiveCallbackSessionThread() {
        synchronized (stateLock) {
            return activeCallbackSessionThread == Thread.currentThread();
        }
    }

    public void waitForConsumerCallbackToComplete(MessageConsumer consumer) throws InterruptedException {
        synchronized (stateLock) {
            while (activeConsumerInCallback == consumer) {
                try {
                    stateLock.wait();
                } catch (InterruptedException e) {
                    LOG.warn(
                            "Interrupted while waiting the active consumer in callback to complete. Continue to wait...",
                            e);
                }
            }
        }
    }

    void waitForCallbackComplete() {
        synchronized (stateLock) {
            while (activeConsumerInCallback != null) {
                try {
                    stateLock.wait();
                } catch (InterruptedException e) {
                    LOG.warn("Interrupted while waiting on session callback completion. Continue to wait...", e);
                }
            }
        }
    }

    @Override
    public void run() {
    }

    @Override
    public MessageProducer createProducer(Destination destination)
            throws JMSException {
        if (destination == null || !(destination instanceof MNSQueueDestination)) {
            throw new JMSException("Actual type of destination must be MNSQueueDestination");
        }

        synchronized (closed) {
            checkClosed();

            MNSMessageProducer producer = new MNSMessageProducer(mnsClientWrapper, this, (MNSQueueDestination) destination);
            messageProducers.add(producer); // TODO atomic operator
            return producer;
        }
    }

    void removeProducer(MNSMessageProducer producer) {
        messageProducers.remove(producer);
    }

    @Override
    public MessageConsumer createConsumer(Destination destination)
            throws JMSException {
        if (destination == null || !(destination instanceof MNSQueueDestination)) {
            throw new JMSException("Actual type of destination must be MNSQueueDestination");
        }

        synchronized (closed) {
            checkClosed();

            MNSMessageConsumer consumer = new MNSMessageConsumer(
                    parentConnection, this, acknowledger,
                    mnsClientWrapper, (MNSQueueDestination) destination,
                    CONSUMER_PREFETCH_THREAD_FACTORY);
            messageConsumers.add(consumer);
            if (running.get()) {
                consumer.startPrefetch();
            }
            return consumer;
        }
    }

    void removeConsumer(MNSMessageConsumer consumer) {
        messageConsumers.remove(consumer);
    }

    @Override
    public Queue createQueue(String queueName) throws JMSException {
        checkClosed();
        return new MNSQueueDestination(queueName);
    }

    @Override
    public QueueReceiver createReceiver(Queue queue) throws JMSException {
        return (QueueReceiver) createConsumer(queue);
    }

    @Override
    public QueueSender createSender(Queue queue) throws JMSException {
        return (QueueSender) createProducer(queue);
    }

    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        checkClosed();
        return new MNSBytesMessage();
    }

    /**
     * sent with only headers without any payload
     */
    @Override
    public Message createMessage() throws JMSException {
        checkClosed();
        return new MNSTextMessage();
    }

    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        checkClosed();
        return new MNSObjectMessage();
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable object)
            throws JMSException {
        checkClosed();
        return new MNSObjectMessage(object);
    }

    @Override
    public TextMessage createTextMessage() throws JMSException {
        checkClosed();
        return new MNSTextMessage();
    }

    @Override
    public TextMessage createTextMessage(String text) throws JMSException {
        checkClosed();
        return new MNSTextMessage(text);
    }

    /* Not support */
    @Override
    public MessageConsumer createConsumer(Destination destination,
                                          String messageSelector) throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /* Not support */
    @Override
    public MessageConsumer createConsumer(Destination destination,
                                          String messageSelector, boolean noLocal) throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not support messageSelector
     */
    @Override
    public QueueReceiver createReceiver(Queue queue, String messageSelector)
            throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public void commit() throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public void rollback() throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public Topic createTopic(String topicName) throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name)
            throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name,
                                                   String messageSelector, boolean noLocal) throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public QueueBrowser createBrowser(Queue queue, String messageSelector)
            throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public void unsubscribe(String name) throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public MapMessage createMapMessage() throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public MessageListener getMessageListener() throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public void setMessageListener(MessageListener listener)
            throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

}
