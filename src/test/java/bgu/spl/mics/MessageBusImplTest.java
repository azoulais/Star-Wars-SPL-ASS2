package bgu.spl.mics;

import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * we changed the {@link SimpleMessageBusTest} since the old test used the fields
 * of the dummy {@link SimpleMessageBus} which are not private.
 * <p>
 * This test is compatible with the required {@link MessageBusImpl}.
 * <p>
 * The logic of the test is the same,
 * but with implementation that can use the {@link MessageBusImpl}.
 */
class MessageBusImplTest {
    private MessageBusImpl bus = MessageBusImpl.getInstance();
    private MicroService hans1 = new HanSoloMicroservice();
    private MicroService hans2 = new HanSoloMicroservice();
    //hans3 used only for testing register
    private MicroService hans3 = new HanSoloMicroservice();
    private MicroService c3po = new C3POMicroservice();
    private SimpleEvent event = new SimpleEvent();
    private SimpleBroadcast broadcast = new SimpleBroadcast();
    private Future<Integer> future = new Future();

    @BeforeEach
    void setUp() {
        bus.register(hans1);
        bus.register(hans2);
        bus.register(c3po);
    }

    @AfterEach
    void tearDown() {
        bus.unregister(hans1);
        bus.unregister(hans2);
        bus.unregister(c3po);
        bus.unregister(hans3);
    }

    @Test
    void subscribeEvent() {
        hans1.subscribeEvent(event.getClass(), (event) -> {
        });
        c3po.sendEvent(event);
        try {
            assertEquals(event, bus.awaitMessage(hans1));
        } catch (InterruptedException e) {
        }
    }

    @Test
    void subscribeBroadcast() {
        hans1.subscribeBroadcast(broadcast.getClass(), (broadcast) -> {
        });
        c3po.sendBroadcast(broadcast);
        try {
            //hans got the brodcast
            assertEquals(bus.awaitMessage(hans1), broadcast);
        } catch (InterruptedException e) {
        }
    }

    @Test
    void complete() {
        Integer someInt = 5;
        hans1.subscribeEvent(event.getClass(), (event) -> {
        });
        future = c3po.sendEvent(event);
        hans1.complete(event, someInt);
        assertTrue(future.isDone());
        assertEquals(someInt, future.get());
    }

    @Test
    void sendBroadcast() {
        hans1.subscribeBroadcast(broadcast.getClass(), (broadcast) -> {
        });
        hans2.subscribeBroadcast(broadcast.getClass(), (broadcast) -> {
        });
        c3po.sendBroadcast(broadcast);
        try {
            //both got the same broadcast
            assertEquals(bus.awaitMessage(hans1), bus.awaitMessage(hans2));
        } catch (InterruptedException e) {
        }
    }

    @Test
    void sendEvent() {
        assertNull(c3po.sendEvent(event));
        hans1.subscribeEvent(event.getClass(), (event) -> {
        });
        assertNotNull(c3po.sendEvent(event));
        try {
            assertEquals(event, bus.awaitMessage(hans1));
        } catch (InterruptedException e) {
        }
    }

    @Test
    /**
     * since private fields of MessageBus are not accessible,
     * we use the awaitmessage which throw {@link IllegalStateException}
     * in case microservice is not registered
     */
    void register() {
        //not registered
        try {
            bus.awaitMessage(hans3).getClass();
        } catch (Exception e) {
            assertEquals(e.getClass(), IllegalStateException.class);
        }
        bus.register(hans3);
        //registered, we will check he can get a message
        hans3.subscribeEvent(event.getClass(), (event) -> {
        });
        c3po.sendEvent(event);
        try {
            assertEquals(event, bus.awaitMessage(hans3));
        } catch (InterruptedException e) {
        }
    }

    @Test
    void awaitMessage() {
        hans1.subscribeEvent(event.getClass(), (event) -> {
        });
        c3po.sendEvent(event);
        try {
            assertEquals(event, bus.awaitMessage(hans1));
        } catch (InterruptedException e) {
        }
    }
}