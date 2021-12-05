package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBattle;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 */
public class R2D2Microservice extends MicroService {
    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeEvent(DeactivationEvent.class, (event) -> {
            try {
                Thread.sleep(duration);
                Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());
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
        Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
    }
}
