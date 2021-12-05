package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBattle;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 */
public class HanSoloMicroservice extends MicroService {
    public HanSoloMicroservice() {
        super("Han");
    }


    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, (event) -> {
                    //  aquire ewoks -> sleep -> release ewoks
                    Ewoks.getInstance().aquireEwoks(event.getSerials());
                    try {
                        Thread.sleep(event.getDuration());
                    } catch (InterruptedException e) {
                    }
                    Diary.getInstance().settotalAttacks();
                    Ewoks.getInstance().releaseEwoks(event.getSerials());
                    complete(event, true);
                    Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
                }
        );
        Main.latch.countDown();
        subscribeBroadcast(TerminateBattle.class, (event) -> {
            terminate();
        });
    }

    @Override
    protected void close() {
        Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
    }


}
