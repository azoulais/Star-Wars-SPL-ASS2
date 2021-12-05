package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;


import bgu.spl.mics.application.messages.TerminateBattle;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
    private Future[] futures;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        futures = new Future[attacks.length];
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBattle.class, (event) -> {
            terminate();
        });

        //wait for the others to subscribe Attackevent
        try {
            Main.latch.await();
        } catch (InterruptedException e) {
        }

        sendAttacks();
        send(new DeactivationEvent());
        send(new BombEvent());
        sendBroadcast(new TerminateBattle());
    }

    /**
     * This method is used by Leia to send the Attack objects as AttackEvents,
     * and wait for the future object to be resolved for each AttackEvent.
     */
    private void sendAttacks() {
        for (int i = 0; i < attacks.length; i++) {
            Attack nextAttack = attacks[i];
            Future<Boolean> future = sendEvent(new AttackEvent(nextAttack.getSerials(), nextAttack.getDuration()));
            futures[i] = future;
        }
        for (int i = 0; i < futures.length; i++) {
            futures[i].get();
        }
    }

    /**
     * This method used by Leia to send the DeactivationEvent and the BombEvent.
     * Leia wait for the future object to be resolved
     *
     * @param event The event to send
     * @param <T>   The type of event result.
     */
    private <T> void send(Event<T> event) {
        Future<T> future = sendEvent(event);
        if (future != null)
            future.get();
    }


    @Override
    protected void close() {
        Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
    }
}
