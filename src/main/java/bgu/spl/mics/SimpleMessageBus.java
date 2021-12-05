package bgu.spl.mics;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleMessageBus implements MessageBus {

    HashMap<Event, Future> eventFutureMap;
    HashMap<Class<? extends Message>, MicroService[]> eventSubsMap;

    HashMap<String, BlockingQueue<Message>> subsQueueMap;


    public SimpleMessageBus() {
        //CTR
        eventSubsMap = new HashMap<>();
        subsQueueMap = new HashMap<>();
        eventFutureMap = new HashMap<>();
    }


    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        sub(type, m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        sub(type, m);
    }

    private void sub(Class type, MicroService m) {
        if (!eventSubsMap.containsKey(type)) {
            eventSubsMap.put(type, new MicroService[2]);
            eventSubsMap.get(type)[0] = m;
            eventSubsMap.get(type)[1] = null;
        } else {
            if (eventSubsMap.get(type)[1] == null)
                eventSubsMap.get(type)[1] = m;
        }
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        if (eventFutureMap.containsKey(e))
            eventFutureMap.get(e).resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        MicroService[] a = eventSubsMap.get(b.getClass());
        for (int i = 0; i < a.length; i++) {
            if (a[i] != null)
                subsQueueMap.get(a[i].getName()).add(b);
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        if (!eventFutureMap.containsKey(e))
            eventFutureMap.put(e, new Future<T>());
        MicroService[] a = eventSubsMap.get(e.getClass());
        if (a != null) {
            subsQueueMap.get(a[0].getName()).add(e);
            return eventFutureMap.get(e);
        }
        return null;
    }

    @Override
    public void register(MicroService m) {
        if (!subsQueueMap.containsKey(m.getName())) {
            subsQueueMap.put(m.getName(), new LinkedBlockingQueue<>());
        }
    }

    @Override
    public void unregister(MicroService m) {
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (!subsQueueMap.get(m.getName()).isEmpty())
            return subsQueueMap.get(m.getName()).remove();
        return null;
    }
}
