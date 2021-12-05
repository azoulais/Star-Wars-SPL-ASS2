package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBattle;
import bgu.spl.mics.application.messages.BombEvent;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice is in charge of the handling {@link BombEvent}.
 *  * This class may not hold references for objects which it is not responsible for:
 *  * {@link BombEvent}.
 */
public class LandoMicroservice extends MicroService {
    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeEvent(BombEvent.class, (event) -> {
            try {
                Thread.sleep(duration);
                complete(event, true);
            } catch (InterruptedException e) {
            }
         });
        subscribeBroadcast(TerminateBattle.class, (event) -> {
            terminate();
        });
    }

    @Override
    protected void close() {
        Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
    }
}
