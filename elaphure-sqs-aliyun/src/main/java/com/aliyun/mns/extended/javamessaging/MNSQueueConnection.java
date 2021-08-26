package com.aliyun.mns.extended.javamessaging;

import com.aliyun.mns.extended.javamessaging.acknowledge.AcknowledgeMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.IllegalStateException;
import javax.jms.*;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class MNSQueueConnection implements Connection, QueueConnection {
    private static final Log LOG = LogFactory.getLog(MNSQueueConnection.class);

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final AtomicBoolean closing = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Object stateLock = new Object();

    private final Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<Session, Boolean>());

    private final MNSClientWrapper mnsClientWrapper;

    public MNSQueueConnection(MNSClientWrapper clientWrapper) {
        this.mnsClientWrapper = clientWrapper;
    }

    @Override
    public QueueSession createQueueSession(boolean transacted,
                                           int acknowledgeMode) throws JMSException {
        return (QueueSession) createSession(transacted, acknowledgeMode);
    }

    /*
     * @param transacted Only false is supported
     */
    @Override
    public Session createSession(boolean transacted, int acknowledgeMode)
            throws JMSException {
        checkClosed();
        if (transacted || acknowledgeMode == Session.SESSION_TRANSACTED) {
            throw new JMSException(Constants.UNSUPPORTED_METHOD);
        }

        MNSQueueSession session;
        if (acknowledgeMode == Session.AUTO_ACKNOWLEDGE) {
            session = new MNSQueueSession(this, AcknowledgeMode.ACK_AUTO.withOriginalAcknowledgeMode(acknowledgeMode));
        } else if (acknowledgeMode == Session.CLIENT_ACKNOWLEDGE || acknowledgeMode == MNSQueueSession.MANUAL_ACKNOWLEDGE) {
            session = new MNSQueueSession(this, AcknowledgeMode.ACK_MANUAL.withOriginalAcknowledgeMode(acknowledgeMode));
        } else {
            LOG.error("Unrecognized acknowledgeMode. Cannot create Session.");
            throw new JMSException("Unrecognized acknowledgeMode. Cannot create Session.");
        }

        synchronized (stateLock) {
            checkClosing();
            sessions.add(session);

            if (running.get()) {
                session.start();
            }
        }

        return session;
    }

    public MNSClientWrapper getMNSClientWrapper() {
        return mnsClientWrapper;
    }

    @Override
    public void start() throws JMSException {
        checkClosed();

        if (running.get()) {
            return;
        }

        synchronized (stateLock) {
            checkClosing();
            if (!running.get()) {
                try {
                    for (Session session : sessions) {
                        MNSQueueSession queueSession = (MNSQueueSession) session;
                        queueSession.start();
                    }
                } finally {
                    running.set(true);
                }
            }
        }
    }

    @Override
    public void stop() throws JMSException {
        checkClosed();

        if (!running.get()) {
            return;
        }

        if (MNSQueueSession.CALLBACK_SCHEDULER_THREAD_FACTORY.wasThreadCreatedWithThisThreadGroup(Thread.currentThread())) {
            throw new IllegalStateException(
                    "MessageListener must not attempt to stop its own Connection to prevent potential deadlock issues");
        }

        synchronized (stateLock) {
            checkClosing();
            if (running.get()) {
                try {
                    for (Session session : sessions) {
                        MNSQueueSession queueSession = (MNSQueueSession) session;
                        queueSession.stop();
                    }
                } finally {
                    running.set(false);
                }
            }
        }
    }

    @Override
    public void close() throws JMSException {
        if (closed.get()) {
            return;
        }

        if (MNSQueueSession.CALLBACK_SCHEDULER_THREAD_FACTORY.wasThreadCreatedWithThisThreadGroup(Thread.currentThread())) {
            throw new IllegalStateException(
                    "MessageListener must not attempt to close its own Connection to prevent potential deadlock issues");
        }

        boolean shouldClose = false;
        synchronized (stateLock) {
            if (!closing.get()) {
                shouldClose = true;
                closing.set(true);
            }
        }

        if (shouldClose) {
            synchronized (stateLock) {
                try {
                    for (Session session : sessions) {
                        MNSQueueSession sqsSession = (MNSQueueSession) session;
                        sqsSession.close();
                    }
                    sessions.clear();
                } finally {
                    closed.set(true);
                    stateLock.notifyAll();

                }
            }
        } else {
            synchronized (stateLock) {
                while (!closed.get()) {
                    try {
                        stateLock.wait();
                    } catch (InterruptedException e) {
                        LOG.error("Interrupted while waiting the session to close.", e);
                    }
                }
            }
        }
    }

    public void removeSession(MNSQueueSession session) {
        sessions.remove(session);
    }

    public void checkClosing() throws IllegalStateException {
        if (closing.get()) {
            throw new IllegalStateException("Connection is closed or closing");
        }
    }

    public void checkClosed() throws IllegalStateException {
        if (closed.get()) {
            throw new IllegalStateException("Connection is closd");
        }
    }

    /**
     * Not supported.
     */
    @Override
    public ConnectionConsumer createConnectionConsumer(Queue queue,
                                                       String messageSelector, ServerSessionPool sessionPool,
                                                       int maxMessages) throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination,
                                                       String messageSelector, ServerSessionPool sessionPool,
                                                       int maxMessages) throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    /**
     * Not supported.
     */
    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic,
                                                              String subscriptionName, String messageSelector,
                                                              ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        throw new JMSException(Constants.UNSUPPORTED_METHOD);
    }

    @Override
    public String getClientID() throws JMSException {
        return null;
    }

    @Override
    public void setClientID(String clientID) throws JMSException {
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return null;
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        return null;
    }

    @Override
    public void setExceptionListener(ExceptionListener listener) throws JMSException {
    }
}
