package bgu.spl.mics;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<ConcurrentLinkedQueue<Message>>> messageTypeToQueueHash;//this hash is matching keys of Event type to its proper queue.
    private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> microServiceToMessageQueueHash;
    private ConcurrentHashMap<Event, Future> eventToFutureHash;
    private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Message>>> microServiceSubscriptions;


    private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private MessageBusImpl() {
        eventToFutureHash = new ConcurrentHashMap();
        messageTypeToQueueHash = new ConcurrentHashMap();
        microServiceToMessageQueueHash = new ConcurrentHashMap<>();
        microServiceSubscriptions = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        subscribe(type, m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        subscribe(type, m);
    }

    private void subscribe(Class<? extends Message> type, MicroService m) {
        ConcurrentLinkedQueue<Message> msQueue = microServiceToMessageQueueHash.get(m);
        if (!messageTypeToQueueHash.containsKey(type)) {
            synchronized (messageTypeToQueueHash) {
                if (!messageTypeToQueueHash.containsKey(type))
                    messageTypeToQueueHash.put(type, new ConcurrentLinkedQueue<>());
            }
        }
        ConcurrentLinkedQueue<ConcurrentLinkedQueue<Message>> messageQueue = messageTypeToQueueHash.get(type);
        messageQueue.add(msQueue);

        microServiceSubscriptions.get(m).add(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {
        Future<T> future = eventToFutureHash.get(e);
        future.resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if (messageTypeToQueueHash.containsKey(b.getClass())) {
            ConcurrentLinkedQueue<ConcurrentLinkedQueue<Message>> mainQueue = messageTypeToQueueHash.get(b.getClass());
            Iterator<ConcurrentLinkedQueue<Message>> iterator = mainQueue.iterator();
            while (iterator.hasNext()) {
                ConcurrentLinkedQueue<Message> microServiceQueue = iterator.next();
                synchronized (microServiceQueue) {
                    microServiceQueue.add(b);
                    microServiceQueue.notifyAll();
                }
            }
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        if (messageTypeToQueueHash.containsKey(e.getClass())) {
            Future<T> future = new Future<>();
            eventToFutureHash.put(e, future);
            ConcurrentLinkedQueue<ConcurrentLinkedQueue<Message>> eventQueue = messageTypeToQueueHash.get(e.getClass());
            synchronized (eventQueue) {
                ConcurrentLinkedQueue<Message> firstQueue = eventQueue.poll();
                if (firstQueue == null)
                    return null;
                synchronized (firstQueue) {
                    firstQueue.add(e);
                    firstQueue.notifyAll();
                }
                eventQueue.add(firstQueue);
            }
            return future;
        }
        return null;
    }

    @Override
    public void register(MicroService m) {
        if (!microServiceToMessageQueueHash.containsKey(m)) {
            ConcurrentLinkedQueue<Message> messagesQueue = new ConcurrentLinkedQueue<>();
            microServiceToMessageQueueHash.put(m, messagesQueue);
            microServiceSubscriptions.put(m, new ConcurrentLinkedQueue<>());
        }
    }


    @Override
    public void unregister(MicroService m) {
        if (microServiceToMessageQueueHash.containsKey(m)) {
            ConcurrentLinkedQueue<Class<? extends Message>> subs = microServiceSubscriptions.get(m);
            ConcurrentLinkedQueue<Message> mQueue = microServiceToMessageQueueHash.get(m);
            Iterator<Class<? extends Message>> iterator = subs.iterator();

            while (iterator.hasNext()) {
                messageTypeToQueueHash.get(iterator.next()).remove(mQueue);
            }
            microServiceToMessageQueueHash.remove(m);
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (!microServiceToMessageQueueHash.containsKey(m)) {
            //case of m not registered
            throw new IllegalStateException();
        }
        ConcurrentLinkedQueue<Message> mQueue = microServiceToMessageQueueHash.get(m);
        synchronized (mQueue) {
            while (mQueue.isEmpty()) {
                mQueue.wait();
            }
        }
        return mQueue.remove();
    }
}
